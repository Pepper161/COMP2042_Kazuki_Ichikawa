package com.comp2042.game.stats;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Persists leaderboard records on the user's machine to survive new sessions.
 */
public final class HighScoreService {

    private static final int MAX_ENTRIES = 10;
    private static final Comparator<HighScoreEntry> SORT_BY_SCORE =
            Comparator.comparingInt(HighScoreEntry::getScore).reversed()
                    .thenComparingLong(HighScoreEntry::getDurationSeconds)
                    .thenComparingLong(HighScoreEntry::getRecordedAtMillis);

    private final Path storagePath;

    public HighScoreService() {
        this(resolveStoragePath());
    }

    HighScoreService(Path storagePath) {
        this.storagePath = Objects.requireNonNull(storagePath);
    }

    public synchronized List<HighScoreEntry> recordScore(HighScoreEntry newEntry) {
        if (newEntry == null) {
            return fetchLeaderboard();
        }
        List<HighScoreEntry> entries = new ArrayList<>(readEntries());
        entries.add(newEntry);
        entries.sort(SORT_BY_SCORE);
        if (entries.size() > MAX_ENTRIES) {
            entries = new ArrayList<>(entries.subList(0, MAX_ENTRIES));
        }
        writeEntries(entries);
        return Collections.unmodifiableList(entries);
    }

    public synchronized List<HighScoreEntry> recordScore(int score, String mode, Duration duration) {
        HighScoreEntry entry = HighScoreEntry.create(score, mode, duration);
        return recordScore(entry);
    }

    public synchronized List<HighScoreEntry> fetchLeaderboard() {
        return Collections.unmodifiableList(new ArrayList<>(readEntries()));
    }

    public synchronized void clear() {
        try {
            Files.deleteIfExists(storagePath);
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to delete leaderboard file: " + ex.getMessage());
        }
    }

    private List<HighScoreEntry> readEntries() {
        if (!Files.exists(storagePath)) {
            return List.of();
        }
        try {
            List<String> lines = Files.readAllLines(storagePath, StandardCharsets.UTF_8);
            List<HighScoreEntry> entries = new ArrayList<>();
            for (String line : lines) {
                HighScoreEntry entry = parseLine(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
            entries.sort(SORT_BY_SCORE);
            if (entries.size() > MAX_ENTRIES) {
                return new ArrayList<>(entries.subList(0, MAX_ENTRIES));
            }
            return entries;
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to read leaderboard file: " + ex.getMessage());
            return List.of();
        }
    }

    private void writeEntries(List<HighScoreEntry> entries) {
        try {
            Files.createDirectories(storagePath.getParent());
            List<String> lines = new ArrayList<>();
            for (HighScoreEntry entry : entries) {
                lines.add(serialize(entry));
            }
            Files.write(storagePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to write leaderboard file: " + ex.getMessage());
        }
    }

    private static HighScoreEntry parseLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) {
            return null;
        }
        try {
            int score = Integer.parseInt(parts[0]);
            String mode = unescape(parts[1]);
            long duration = Long.parseLong(parts[2]);
            long recordedAt = Long.parseLong(parts[3]);
            return HighScoreEntry.recreate(score, mode, duration, recordedAt);
        } catch (NumberFormatException ex) {
            System.err.println("[HighScoreService] Skipping malformed record: " + line);
            return null;
        }
    }

    private static String serialize(HighScoreEntry entry) {
        return entry.getScore() + "|"
                + escape(entry.getMode()) + "|"
                + entry.getDurationSeconds() + "|"
                + entry.getRecordedAtMillis();
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("|", "/");
    }

    private static String unescape(String raw) {
        return raw == null ? "" : raw;
    }

    private static Path resolveStoragePath() {
        String home = System.getProperty("user.home", ".");
        Path directory = Path.of(home, ".comp2042");
        return directory.resolve("highscores.dat");
    }
}
