package com.comp2042.config;

import javafx.scene.input.KeyCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot capturing the player's gameplay preferences (key bindings, DAS/ARR tuning, audio options).
 * <p>
 * Instances are produced via the {@link Builder} to guarantee sensible defaults and simple cloning into a mutable
 * instance before persisting with {@link GameSettingsStore}.
 * </p>
 */
public final class GameSettings {

    /**
     * Color palette presets exposed in the settings dialog.
     */
    public enum ColorAssistMode {
        CLASSIC("Classic"),
        HIGH_CONTRAST("High Contrast");

        private final String displayName;

        ColorAssistMode(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Logical key-bindable actions supported by the UI.
     */
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
    private final ColorAssistMode colorAssistMode;
    private final boolean pieceOutlineEnabled;

    private GameSettings(Builder builder) {
        this.dasDelayMs = builder.dasDelayMs;
        this.arrIntervalMs = builder.arrIntervalMs;
        this.softDropMultiplier = builder.softDropMultiplier;
        this.keyBindings = new EnumMap<>(builder.keyBindings);
        this.bgmEnabled = builder.bgmEnabled;
        this.bgmVolume = builder.bgmVolume;
        this.colorAssistMode = builder.colorAssistMode;
        this.pieceOutlineEnabled = builder.pieceOutlineEnabled;
    }

    /**
     * Creates a new builder with guideline defaults.
     *
     * @return builder pre-populated with safe defaults
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Shortcut to obtain a default, immutable settings object.
     *
     * @return default gameplay settings
     */
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

    public ColorAssistMode getColorAssistMode() {
        return colorAssistMode;
    }

    public boolean isPieceOutlineEnabled() {
        return pieceOutlineEnabled;
    }

    /**
     * Creates a new builder seeded from this immutable instance so callers can adjust a copy.
     *
     * @return mutable builder seeded with the current values
     */
    public Builder toBuilder() {
        return builder().from(this);
    }

    /**
     * Mutable builder with validation steps on every setter to guarantee sane persisted values.
     */
    public static final class Builder {

        private long dasDelayMs = 220;
        private long arrIntervalMs = 60;
        private double softDropMultiplier = 8.0;
        private final EnumMap<Action, KeyCode> keyBindings = new EnumMap<>(Action.class);
        private boolean bgmEnabled = true;
        private double bgmVolume = 0.35;
        private ColorAssistMode colorAssistMode = ColorAssistMode.CLASSIC;
        private boolean pieceOutlineEnabled = false;

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
            setColorAssistMode(settings.getColorAssistMode());
            setPieceOutlineEnabled(settings.isPieceOutlineEnabled());
            return this;
        }

        /**
         * Sets the delay before horizontal auto-shift begins.
         *
         * @param dasDelayMs duration in milliseconds; clamped to non-negative values
         * @return self for chaining
         */
        public Builder setDasDelayMs(long dasDelayMs) {
            this.dasDelayMs = Math.max(0, dasDelayMs);
            return this;
        }

        /**
         * Sets the auto-repeat interval for horizontal movement.
         *
         * @param arrIntervalMs duration in milliseconds; clamped to non-negative values
         * @return self for chaining
         */
        public Builder setArrIntervalMs(long arrIntervalMs) {
            this.arrIntervalMs = Math.max(0, arrIntervalMs);
            return this;
        }

        /**
         * Controls the multiplier applied to soft-drop gravity.
         *
         * @param softDropMultiplier multiplier, clamped to 0.1 or higher
         * @return self for chaining
         */
        public Builder setSoftDropMultiplier(double softDropMultiplier) {
            this.softDropMultiplier = Math.max(0.1, softDropMultiplier);
            return this;
        }

        /**
         * Overrides the key binding for the requested logical action.
         *
         * @param action logical action
         * @param keyCode javaFX key code
         * @return self for chaining
         */
        public Builder setKey(Action action, KeyCode keyCode) {
            keyBindings.put(Objects.requireNonNull(action), Objects.requireNonNull(keyCode));
            return this;
        }

        /**
         * Enables or disables background music playback.
         *
         * @param enabled true when BGM should play
         * @return self for chaining
         */
        public Builder setBgmEnabled(boolean enabled) {
            this.bgmEnabled = enabled;
            return this;
        }

        /**
         * Sets the normalized background music volume (0.0 - 1.0).
         *
         * @param volume normalized volume; clamped into range
         * @return self for chaining
         */
        public Builder setBgmVolume(double volume) {
            this.bgmVolume = Math.max(0.0, Math.min(1.0, volume));
            return this;
        }

        /**
         * Selects the color-assist preset.
         *
         * @param mode selected color mode
         * @return self for chaining
         */
        public Builder setColorAssistMode(ColorAssistMode mode) {
            this.colorAssistMode = mode != null ? mode : ColorAssistMode.CLASSIC;
            return this;
        }

        /**
         * Toggles the visible outline that helps distinguish pieces on similar backgrounds.
         *
         * @param enabled true to show outlines
         * @return self for chaining
         */
        public Builder setPieceOutlineEnabled(boolean enabled) {
            this.pieceOutlineEnabled = enabled;
            return this;
        }

        /**
         * Produces the immutable snapshot.
         *
         * @return immutable {@link GameSettings}
         */
        public GameSettings build() {
            return new GameSettings(this);
        }
    }
}
