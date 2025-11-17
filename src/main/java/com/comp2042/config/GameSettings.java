package com.comp2042.config;

import javafx.scene.input.KeyCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable input/settings snapshot containing DAS/ARR/SDF tuning and key bindings.
 */
public final class GameSettings {

    public enum Action {
        MOVE_LEFT,
        MOVE_RIGHT,
        SOFT_DROP,
        HARD_DROP,
        ROTATE_CW,
        ROTATE_CCW,
        NEW_GAME
    }

    private final long dasDelayMs;
    private final long arrIntervalMs;
    private final double softDropMultiplier;
    private final EnumMap<Action, KeyCode> keyBindings;
    private final boolean bgmEnabled;
    private final double bgmVolume;

    private GameSettings(Builder builder) {
        this.dasDelayMs = builder.dasDelayMs;
        this.arrIntervalMs = builder.arrIntervalMs;
        this.softDropMultiplier = builder.softDropMultiplier;
        this.keyBindings = new EnumMap<>(builder.keyBindings);
        this.bgmEnabled = builder.bgmEnabled;
        this.bgmVolume = builder.bgmVolume;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static GameSettings defaultSettings() {
        return builder().build();
    }

    public long getDasDelayMs() {
        return dasDelayMs;
    }

    public long getArrIntervalMs() {
        return arrIntervalMs;
    }

    public double getSoftDropMultiplier() {
        return softDropMultiplier;
    }

    public KeyCode getKey(Action action) {
        return keyBindings.get(action);
    }

    public Map<Action, KeyCode> getKeyBindings() {
        return Collections.unmodifiableMap(keyBindings);
    }

    public boolean isBgmEnabled() {
        return bgmEnabled;
    }

    public double getBgmVolume() {
        return bgmVolume;
    }

    public Builder toBuilder() {
        return builder().from(this);
    }

    public static final class Builder {

        private long dasDelayMs = 220;
        private long arrIntervalMs = 60;
        private double softDropMultiplier = 8.0;
        private final EnumMap<Action, KeyCode> keyBindings = new EnumMap<>(Action.class);
        private boolean bgmEnabled = true;
        private double bgmVolume = 0.35;

        private Builder() {
            keyBindings.put(Action.MOVE_LEFT, KeyCode.LEFT);
            keyBindings.put(Action.MOVE_RIGHT, KeyCode.RIGHT);
            keyBindings.put(Action.SOFT_DROP, KeyCode.DOWN);
            keyBindings.put(Action.HARD_DROP, KeyCode.SPACE);
            keyBindings.put(Action.ROTATE_CW, KeyCode.UP);
            keyBindings.put(Action.ROTATE_CCW, KeyCode.Z);
            keyBindings.put(Action.NEW_GAME, KeyCode.N);
        }

        private Builder from(GameSettings settings) {
            if (settings == null) {
                return this;
            }
            setDasDelayMs(settings.dasDelayMs);
            setArrIntervalMs(settings.arrIntervalMs);
            setSoftDropMultiplier(settings.softDropMultiplier);
            keyBindings.clear();
            keyBindings.putAll(settings.keyBindings);
            setBgmEnabled(settings.isBgmEnabled());
            setBgmVolume(settings.getBgmVolume());
            return this;
        }

        public Builder setDasDelayMs(long dasDelayMs) {
            this.dasDelayMs = Math.max(0, dasDelayMs);
            return this;
        }

        public Builder setArrIntervalMs(long arrIntervalMs) {
            this.arrIntervalMs = Math.max(0, arrIntervalMs);
            return this;
        }

        public Builder setSoftDropMultiplier(double softDropMultiplier) {
            this.softDropMultiplier = Math.max(0.1, softDropMultiplier);
            return this;
        }

        public Builder setKey(Action action, KeyCode keyCode) {
            keyBindings.put(Objects.requireNonNull(action), Objects.requireNonNull(keyCode));
            return this;
        }

        public Builder setBgmEnabled(boolean enabled) {
            this.bgmEnabled = enabled;
            return this;
        }

        public Builder setBgmVolume(double volume) {
            this.bgmVolume = Math.max(0.0, Math.min(1.0, volume));
            return this;
        }

        public GameSettings build() {
            return new GameSettings(this);
        }
    }
}
