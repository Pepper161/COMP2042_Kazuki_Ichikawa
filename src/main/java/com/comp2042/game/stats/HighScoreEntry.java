package com.comp2042.game.stats;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Immutable value object describing a single leaderboard row (score, mode, clear time, timestamp).
 * <p>
 * Instances are normally created by {@link HighScoreService} when a game completes but the class also exposes
 * {@link #recreate(int, String, long, long)} for reconstructing entries from disk.
 * </p>
 */
public final class HighScoreEntry {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final int score;
    private final String mode;
    private final long durationSeconds;
    private final long recordedAtMillis;

    private HighScoreEntry(int score, String mode, long durationSeconds, long recordedAtMillis) {
        this.score = Math.max(0, score);
        this.mode = mode == null ? "Classic" : mode;
        this.durationSeconds = Math.max(0, durationSeconds);
        this.recordedAtMillis = recordedAtMillis;
    }

    /**
     * Creates a fresh entry at the current timestamp from runtime results.
     *
     * @param score            achieved score
     * @param mode             display label describing the active {@code GameMode}
     * @param duration         elapsed gameplay duration; may be {@code null}
     * @return immutable leaderboard entry
     */
    public static HighScoreEntry create(int score, String mode, Duration duration) {
        long recordedAt = System.currentTimeMillis();
        long seconds = duration != null ? Math.max(0L, duration.toSeconds()) : 0L;
        return new HighScoreEntry(score, normalizeMode(mode), seconds, recordedAt);
    }

    /**
     * Rebuilds an entry from persisted fields.
     *
     * @param score            stored score
     * @param mode             stored mode label
     * @param durationSeconds  stored duration in seconds
     * @param recordedAtMillis stored timestamp in millis since epoch
     * @return immutable leaderboard entry
     */
    public static HighScoreEntry recreate(int score, String mode, long durationSeconds, long recordedAtMillis) {
        return new HighScoreEntry(score, normalizeMode(mode), durationSeconds, recordedAtMillis);
    }

    public int getScore() {
        return score;
    }

    public String getMode() {
        return mode;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public long getRecordedAtMillis() {
        return recordedAtMillis;
    }

    public Instant getRecordedAtInstant() {
        return Instant.ofEpochMilli(recordedAtMillis);
    }

    /**
     * Formats the duration as either {@code HH:mm:ss} or {@code mm:ss} depending on the length.
     *
     * @return human readable elapsed time
     */
    public String formattedDuration() {
        long totalSeconds = Math.max(0L, durationSeconds);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Formats the timestamp using the JVM's default time zone for display in the leaderboard.
     *
     * @return formatted timestamp string
     */
    public String formattedTimestamp() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(getRecordedAtInstant(), ZoneId.systemDefault());
        return TIMESTAMP_FORMAT.format(dateTime);
    }

    private static String normalizeMode(String mode) {
        return (mode == null || mode.isBlank()) ? "Classic" : mode.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HighScoreEntry entry)) {
            return false;
        }
        return score == entry.score
                && durationSeconds == entry.durationSeconds
                && recordedAtMillis == entry.recordedAtMillis
                && Objects.equals(mode, entry.mode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, mode, durationSeconds, recordedAtMillis);
    }

    @Override
    public String toString() {
        return "HighScoreEntry{" +
                "score=" + score +
                ", mode='" + mode + '\'' +
                ", durationSeconds=" + durationSeconds +
                ", recordedAtMillis=" + recordedAtMillis +
                '}';
    }
}
