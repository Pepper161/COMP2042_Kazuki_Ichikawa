package com.comp2042.game;

import java.util.OptionalLong;

/**
 * Holds runtime configuration toggles (currently the deterministic seed option).
 */
public final class GameConfig {

    public enum GameMode {
        ENDLESS("Endless"),
        TIMED("Timed (180s)"),
        FIXED_LINES("Fixed 40 lines");

        private final String displayLabel;

        GameMode(String displayLabel) {
            this.displayLabel = displayLabel;
        }

        @Override
        public String toString() {
            return displayLabel;
        }
    }

    private final Long seedOverride;
    private final GameMode mode;

    private GameConfig(Long seedOverride, GameMode mode) {
        this.seedOverride = seedOverride;
        this.mode = mode != null ? mode : GameMode.ENDLESS;
    }

    public static GameConfig defaultConfig() {
        return new GameConfig(null, GameMode.ENDLESS);
    }

    public static GameConfig fromSeedParameter(String rawSeed) {
        if (rawSeed == null || rawSeed.isBlank()) {
            return defaultConfig();
        }
        try {
            return new GameConfig(Long.parseLong(rawSeed.trim()), GameMode.ENDLESS);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Seed must be a signed long, but got: " + rawSeed, ex);
        }
    }

    public OptionalLong seedOverride() {
        return seedOverride != null ? OptionalLong.of(seedOverride) : OptionalLong.empty();
    }

    public boolean hasSeedOverride() {
        return seedOverride != null;
    }

    public GameMode getMode() {
        return mode;
    }

    public GameConfig withMode(GameMode newMode) {
        return new GameConfig(seedOverride, newMode);
    }
}
