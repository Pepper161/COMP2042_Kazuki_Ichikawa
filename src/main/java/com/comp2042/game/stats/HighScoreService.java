package com.comp2042.game.stats;

import com.comp2042.game.GameConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Persists leaderboard records on the user's machine to survive new sessions.
 * <p>
 * The backing file defaults to {@code ~/.comp2042/highscores.dat} and can be overridden via:
 * </p>
 * <ol>
 *     <li>{@link #HIGHSCORES_FILE_PROPERTY} &mdash; explicit file path</li>
 *     <li>{@link #HIGHSCORES_DIR_PROPERTY} &mdash; directory that contains {@value #FILE_NAME}</li>
 *     <li>{@link #DATA_DIR_PROPERTY} &mdash; shared data directory also leveraged by {@link com.comp2042.config.GameSettingsStore}</li>
 * </ol>
 * Writes are flushed to a temporary file and renamed atomically to avoid corruption if the JVM exits mid-write.
 */
public final class HighScoreService {

    public static final String HIGHSCORES_FILE_PROPERTY = "tetris.highscores.file";
    public static final String HIGHSCORES_DIR_PROPERTY = "tetris.highscores.dir";
    public static final String DATA_DIR_PROPERTY = "tetris.data.dir";

    private static final String DEFAULT_DIRECTORY = ".comp2042";
    private static final String FILE_NAME = "highscores.dat";
    private static final int MAX_ENTRIES_PER_MODE = 10;
    private static final Comparator<HighScoreEntry> SORT_BY_SCORE =
            Comparator.comparingInt(HighScoreEntry::getScore).reversed()
                    .thenComparingLong(HighScoreEntry::getDurationSeconds)
                    .thenComparingLong(HighScoreEntry::getRecordedAtMillis);

    private final Path storagePath;

    public HighScoreService() {
        this(resolveStoragePath());
    }

    HighScoreService(Path storagePath) {
        this.storagePath = Objects.requireNonNull(storagePath).toAbsolutePath().normalize();
    }

    /**
     * Records a new entry, keeping only the top {@value #MAX_ENTRIES_PER_MODE} scores per mode.
     *
     * @param newEntry entry to persist
     * @return immutable leaderboard snapshot after insertion
     */
    public synchronized List<HighScoreEntry> recordScore(HighScoreEntry newEntry) {
        if (newEntry == null) {
            return fetchLeaderboard();
        }
        List<HighScoreEntry> entries = new ArrayList<>(readEntries());
        entries.add(newEntry);
        entries.sort(SORT_BY_SCORE);
        entries = pruneByMode(entries);
        writeEntries(entries);
        return Collections.unmodifiableList(entries);
    }

    /**
     * Convenience overload to create a {@link HighScoreEntry} from raw gameplay data.
     */
    public synchronized List<HighScoreEntry> recordScore(int score, String mode, Duration duration) {
        HighScoreEntry entry = HighScoreEntry.create(score, mode, duration);
        return recordScore(entry);
    }

    /**
     * Returns the entire leaderboard sorted by the configured comparator.
     */
    public synchronized List<HighScoreEntry> fetchLeaderboard() {
        return Collections.unmodifiableList(new ArrayList<>(readEntries()));
    }

    /**
     * Returns entries filtered by the provided mode label (string version).
     */
    public synchronized List<HighScoreEntry> fetchLeaderboardForMode(String modeLabel) {
        return fetchLeaderboardForMode(resolveModeKey(modeLabel));
    }

    /**
     * Returns entries filtered by the provided enum value.
     */
    public synchronized List<HighScoreEntry> fetchLeaderboardForMode(GameConfig.GameMode mode) {
        GameConfig.GameMode target = mode != null ? mode : GameConfig.GameMode.ENDLESS;
        List<HighScoreEntry> entries = new ArrayList<>(readEntries());
        List<HighScoreEntry> filtered = new ArrayList<>();
        for (HighScoreEntry entry : entries) {
            if (resolveModeKey(entry.getMode()) == target) {
                filtered.add(entry);
            }
        }
        return Collections.unmodifiableList(filtered);
    }

    /**
     * Deletes the leaderboard file from disk (best-effort).
     */
    public synchronized void clear() {
        try {
            Files.deleteIfExists(storagePath);
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to delete leaderboard file: " + ex.getMessage());
        }
    }

    /**
     * Returns the resolved storage path so diagnostics/tests can assert location.
     *
     * @return absolute path to the leaderboard file
     */
    public Path getStoragePath() {
        return storagePath;
    }

    private List<HighScoreEntry> readEntries() {
        if (!Files.exists(storagePath)) {
            return List.of();
        }
        List<HighScoreEntry> entries = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(storagePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                HighScoreEntry entry = parseLine(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to read leaderboard file: " + ex.getMessage());
            return List.of();
        }
        entries.sort(SORT_BY_SCORE);
        return pruneByMode(entries);
    }

    private void writeEntries(List<HighScoreEntry> entries) {
        Path parent = storagePath.getParent();
        Path tempDir = parent != null ? parent : storagePath.toAbsolutePath().getParent();
        if (tempDir == null) {
            tempDir = Paths.get(".").toAbsolutePath().normalize();
        }
        Path tempFile = null;
        try {
            Files.createDirectories(tempDir);
            tempFile = Files.createTempFile(tempDir, FILE_NAME, ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
                for (HighScoreEntry entry : entries) {
                    writer.write(serialize(entry));
                    writer.newLine();
                }
            }
            moveAtomically(tempFile, storagePath);
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to write leaderboard file: " + ex.getMessage());
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // nothing else to do
                }
            }
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
        String overrideFile = System.getProperty(HIGHSCORES_FILE_PROPERTY);
        if (overrideFile != null && !overrideFile.isBlank()) {
            return Paths.get(overrideFile.trim()).toAbsolutePath().normalize();
        }
        Path directory = resolveStorageDirectory();
        return directory.resolve(FILE_NAME);
    }

    private static Path resolveStorageDirectory() {
        String overrideDir = System.getProperty(HIGHSCORES_DIR_PROPERTY);
        if (overrideDir != null && !overrideDir.isBlank()) {
            return Paths.get(overrideDir.trim()).toAbsolutePath().normalize();
        }
        String dataDir = System.getProperty(DATA_DIR_PROPERTY);
        if (dataDir != null && !dataDir.isBlank()) {
            return Paths.get(dataDir.trim()).toAbsolutePath().normalize();
        }
        String home = System.getProperty("user.home", ".");
        return Paths.get(home, DEFAULT_DIRECTORY).toAbsolutePath().normalize();
    }

    private static void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private List<HighScoreEntry> pruneByMode(List<HighScoreEntry> entries) {
        Map<GameConfig.GameMode, Integer> perMode = new EnumMap<>(GameConfig.GameMode.class);
        List<HighScoreEntry> pruned = new ArrayList<>();
        for (HighScoreEntry entry : entries) {
            GameConfig.GameMode modeKey = resolveModeKey(entry.getMode());
            int count = perMode.getOrDefault(modeKey, 0);
            if (count < MAX_ENTRIES_PER_MODE) {
                pruned.add(entry);
                perMode.put(modeKey, count + 1);
            }
        }
        return pruned;
    }

    private GameConfig.GameMode resolveModeKey(String label) {
        if (label == null || label.isBlank()) {
            return GameConfig.GameMode.ENDLESS;
        }
        String trimmed = label.trim();
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            if (mode.toString().equalsIgnoreCase(trimmed) || mode.name().equalsIgnoreCase(trimmed)) {
                return mode;
            }
        }
        String normalized = trimmed.toLowerCase(Locale.ROOT);
        if ("classic".equals(normalized) || normalized.startsWith("endless")) {
            return GameConfig.GameMode.ENDLESS;
        }
        if (normalized.startsWith("timed")) {
            return GameConfig.GameMode.TIMED;
        }
        if (normalized.startsWith("fixed") || normalized.contains("40")) {
            return GameConfig.GameMode.FIXED_LINES;
        }
        return GameConfig.GameMode.ENDLESS;
    }
}
