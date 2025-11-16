package com.comp2042.game;

import java.util.OptionalLong;

/**
 * Holds runtime configuration toggles (currently the deterministic seed option).
 */
public final class GameConfig {

    private final Long seedOverride;

    private GameConfig(Long seedOverride) {
        this.seedOverride = seedOverride;
    }

    public static GameConfig defaultConfig() {
        return new GameConfig(null);
    }

    public static GameConfig fromSeedParameter(String rawSeed) {
        if (rawSeed == null || rawSeed.isBlank()) {
            return defaultConfig();
        }
        try {
            return new GameConfig(Long.parseLong(rawSeed.trim()));
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
}
