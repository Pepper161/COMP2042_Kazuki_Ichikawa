package com.comp2042.config;

import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.Properties;

/**
 * Persists {@link GameSettings} to a user-scoped properties file (~/.tetrisjfx/settings.properties).
 */
public class GameSettingsStore {

    private static final String FILE_NAME = "settings.properties";
    private static final String KEY_PREFIX = "key.";

    private final Path baseDirectory;
    private final Path settingsFile;

    public GameSettingsStore() {
        this(resolveBaseDirectory());
    }

    GameSettingsStore(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.settingsFile = baseDirectory.resolve(FILE_NAME);
    }

    public GameSettings load() {
        if (!Files.exists(settingsFile)) {
            return GameSettings.defaultSettings();
        }
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(settingsFile)) {
            properties.load(in);
            return fromProperties(properties);
        } catch (IOException ex) {
            System.err.println("[Settings] Failed to read " + settingsFile + ". Using defaults.");
            return GameSettings.defaultSettings();
        }
    }

    public void save(GameSettings settings) {
        Properties properties = toProperties(settings);
        try {
            Files.createDirectories(baseDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create settings directory: " + baseDirectory, ex);
        }
        try (OutputStream out = Files.newOutputStream(settingsFile)) {
            properties.store(out, "TetrisJFX gameplay settings");
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write settings file: " + settingsFile, ex);
        }
    }

    private GameSettings fromProperties(Properties properties) {
        GameSettings.Builder builder = GameSettings.builder();
        parseLong(properties.getProperty("dasDelayMs")).ifPresent(builder::setDasDelayMs);
        parseLong(properties.getProperty("arrIntervalMs")).ifPresent(builder::setArrIntervalMs);
        parseDouble(properties.getProperty("softDropMultiplier")).ifPresent(builder::setSoftDropMultiplier);
        for (GameSettings.Action action : GameSettings.Action.values()) {
            String value = properties.getProperty(KEY_PREFIX + action.name());
            if (value == null || value.isBlank()) {
                continue;
            }
            try {
                builder.setKey(action, KeyCode.valueOf(value.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid key names and fall back to defaults.
            }
        }
        return builder.build();
    }

    private Properties toProperties(GameSettings settings) {
        Properties properties = new Properties();
        properties.setProperty("dasDelayMs", Long.toString(settings.getDasDelayMs()));
        properties.setProperty("arrIntervalMs", Long.toString(settings.getArrIntervalMs()));
        properties.setProperty("softDropMultiplier", Double.toString(settings.getSoftDropMultiplier()));
        for (var entry : settings.getKeyBindings().entrySet()) {
            properties.setProperty(KEY_PREFIX + entry.getKey().name(), entry.getValue().name());
        }
        return properties;
    }

    private static OptionalLong parseLong(String value) {
        if (value == null) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Long.parseLong(value.trim()));
        } catch (NumberFormatException ex) {
            return OptionalLong.empty();
        }
    }

    private static OptionalDouble parseDouble(String value) {
        if (value == null) {
            return OptionalDouble.empty();
        }
        try {
            return OptionalDouble.of(Double.parseDouble(value.trim()));
        } catch (NumberFormatException ex) {
            return OptionalDouble.empty();
        }
    }

    private static Path resolveBaseDirectory() {
        String override = System.getProperty("tetris.settings.dir");
        if (override != null && !override.isBlank()) {
            return Paths.get(override);
        }
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, ".tetrisjfx");
    }
}
