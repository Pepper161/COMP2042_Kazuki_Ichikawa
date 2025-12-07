package com.comp2042.game;

import java.util.Arrays;

/**
 * Tracks Tetris guideline level progression and provides the gravity interval
 * associated with each level so GUI/control layers can remain agnostic.
 */
public final class LevelProgression {

    public static final int DEFAULT_LINES_PER_LEVEL = 10;
    public static final double DEFAULT_BASE_GRAVITY_MS = 400d;
    public static final double DEFAULT_MIN_GRAVITY_MS = 80d;
    private static final double[] DEFAULT_GRAVITY_TABLE = {
            400, 390, 380, 370, 360, 350, 340, 330, 320, 310,
            300, 290, 280, 270, 260, 250, 240, 230, 220, 210,
            200, 190, 185, 180, 175, 170, 165, 160, 155, 150,
            145, 140, 135, 130, 125, 120, 115, 110, 105, 100,
            95, 90, 85, 80
    };

    private final int linesPerLevel;
    private final double baseGravityMs;
    private final double minGravityMs;
    private final double[] gravityTable;

    private int currentLevel;
    private int linesUntilNextLevel;

    public LevelProgression() {
        this(DEFAULT_LINES_PER_LEVEL, DEFAULT_BASE_GRAVITY_MS, DEFAULT_MIN_GRAVITY_MS, DEFAULT_GRAVITY_TABLE);
    }

    public LevelProgression(int linesPerLevel,
                            double baseGravityMs,
                            double minGravityMs,
                            double[] gravityTable) {
        if (linesPerLevel <= 0) {
            throw new IllegalArgumentException("linesPerLevel must be positive");
        }
        this.linesPerLevel = linesPerLevel;
        this.baseGravityMs = baseGravityMs;
        this.minGravityMs = minGravityMs;
        this.gravityTable = gravityTable == null
                ? Arrays.copyOf(DEFAULT_GRAVITY_TABLE, DEFAULT_GRAVITY_TABLE.length)
                : Arrays.copyOf(gravityTable, gravityTable.length);
        reset();
    }

    /**
     * Resets the tracker to level 1 and returns the updated state.
     */
    public LevelState reset() {
        currentLevel = 1;
        linesUntilNextLevel = linesPerLevel;
        return snapshot(false);
    }

    /**
     * Applies the specified number of cleared lines and returns the resulting state.
     */
    public LevelState handleLinesCleared(int linesCleared) {
        if (linesCleared <= 0) {
            return snapshot(false);
        }
        linesUntilNextLevel -= linesCleared;
        boolean leveledUp = false;
        while (linesUntilNextLevel <= 0) {
            currentLevel++;
            linesUntilNextLevel += linesPerLevel;
            leveledUp = true;
        }
        return snapshot(leveledUp);
    }

    public LevelState getState() {
        return snapshot(false);
    }

    private LevelState snapshot(boolean leveledUp) {
        return new LevelState(currentLevel, linesUntilNextLevel, resolveGravityInterval(currentLevel), leveledUp);
    }

    private double resolveGravityInterval(int level) {
        if (level <= 0) {
            return baseGravityMs;
        }
        if (level > gravityTable.length) {
            return minGravityMs;
        }
        return Math.max(minGravityMs, gravityTable[level - 1]);
    }

    /**
     * Immutable view of the current level progression state.
     */
    public record LevelState(int level, int linesUntilNextLevel, double gravityIntervalMs, boolean leveledUp) {
    }
}
