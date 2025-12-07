package com.comp2042.config;

import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.Properties;

/**
 * Persists {@link GameSettings} to a portable properties file (defaults to {@code ~/.tetrisjfx/settings.properties}).
 * <p>
 * Tests and power users can override the storage location with the following system properties (highest priority first):
 * </p>
 * <ol>
 *     <li>{@code tetris.settings.file} &mdash; explicit path to the file</li>
 *     <li>{@code tetris.settings.dir} &mdash; directory that will contain {@code settings.properties}</li>
 *     <li>{@code tetris.data.dir} &mdash; shared data directory used by both settings and high scores</li>
 * </ol>
 * Writes are performed via a temporary file rename so half-written files are avoided on crash or power loss.
 */
public class GameSettingsStore {

    public static final String SETTINGS_FILE_PROPERTY = "tetris.settings.file";
    public static final String SETTINGS_DIR_PROPERTY = "tetris.settings.dir";
    public static final String DATA_DIR_PROPERTY = "tetris.data.dir";

    private static final String FILE_NAME = "settings.properties";
    private static final String DEFAULT_DIRECTORY = ".tetrisjfx";
    private static final String KEY_PREFIX = "key.";
    private static final String COLOR_ASSIST_KEY = "colorAssistMode";
    private static final String OUTLINE_KEY = "outlineEnabled";

    private final Path baseDirectory;
    private final Path settingsFile;

    public GameSettingsStore() {
        this(resolveLocation());
    }

    GameSettingsStore(Path baseDirectory) {
        this(baseDirectory, baseDirectory.resolve(FILE_NAME));
    }

    GameSettingsStore(Path baseDirectory, Path explicitFile) {
        this(new Location(baseDirectory, explicitFile));
    }

    private GameSettingsStore(Location location) {
        this.baseDirectory = location.baseDir;
        this.settingsFile = location.file;
    }

    /**
     * Loads persisted settings or falls back to {@link GameSettings#defaultSettings()} if the file is missing
     * or unreadable.
     *
     * @return in-memory immutable settings snapshot
     */
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

    /**
     * Writes the provided settings atomically to the properties file, creating the target directory if necessary.
     *
     * @param settings snapshot to persist
     */
    public void save(GameSettings settings) {
        if (settings == null) {
            return;
        }
        Properties properties = toProperties(settings);
        Path tempFile = null;
        try {
            Files.createDirectories(baseDirectory);
            tempFile = Files.createTempFile(baseDirectory, FILE_NAME, ".tmp");
            try (OutputStream out = Files.newOutputStream(tempFile)) {
                properties.store(out, "TetrisJFX gameplay settings");
            }
            moveAtomically(tempFile, settingsFile);
        } catch (IOException ex) {
            System.err.println("[Settings] Failed to write " + settingsFile + ": " + ex.getMessage());
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // Nothing else we can do.
                }
            }
        }
    }

    private GameSettings fromProperties(Properties properties) {
        GameSettings.Builder builder = GameSettings.builder();
        parseLong(properties.getProperty("dasDelayMs")).ifPresent(builder::setDasDelayMs);
        parseLong(properties.getProperty("arrIntervalMs")).ifPresent(builder::setArrIntervalMs);
        parseDouble(properties.getProperty("softDropMultiplier")).ifPresent(builder::setSoftDropMultiplier);
        String bgmValue = properties.getProperty("bgmEnabled");
        if (bgmValue != null) {
            builder.setBgmEnabled(Boolean.parseBoolean(bgmValue));
        }
        parseDouble(properties.getProperty("bgmVolume")).ifPresent(builder::setBgmVolume);
        String assistValue = properties.getProperty(COLOR_ASSIST_KEY);
        if (assistValue != null) {
            try {
                builder.setColorAssistMode(GameSettings.ColorAssistMode.valueOf(assistValue.trim()));
            } catch (IllegalArgumentException ignored) {
                builder.setColorAssistMode(GameSettings.ColorAssistMode.CLASSIC);
            }
        }
        String outlineValue = properties.getProperty(OUTLINE_KEY);
        if (outlineValue != null) {
            builder.setPieceOutlineEnabled(Boolean.parseBoolean(outlineValue));
        }
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
        properties.setProperty("bgmEnabled", Boolean.toString(settings.isBgmEnabled()));
        properties.setProperty("bgmVolume", Double.toString(settings.getBgmVolume()));
        properties.setProperty(COLOR_ASSIST_KEY, settings.getColorAssistMode().name());
        properties.setProperty(OUTLINE_KEY, Boolean.toString(settings.isPieceOutlineEnabled()));
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

    /**
     * Returns the resolved base directory so callers (tests) can assert the location.
     *
     * @return directory containing the settings file
     */
    public Path getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Returns the resolved settings file which may be overridden by system properties.
     *
     * @return path to the backing file
     */
    public Path getSettingsFile() {
        return settingsFile;
    }

    private static void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Location resolveLocation() {
        String fileOverride = System.getProperty(SETTINGS_FILE_PROPERTY);
        if (fileOverride != null && !fileOverride.isBlank()) {
            Path file = Paths.get(fileOverride.trim()).toAbsolutePath().normalize();
            Path parent = file.getParent();
            if (parent == null) {
                parent = file.toAbsolutePath().getParent();
            }
            return new Location(parent == null ? Paths.get(".").toAbsolutePath().normalize() : parent, file);
        }
        Path baseDir = resolveBaseDirectory();
        return new Location(baseDir, baseDir.resolve(FILE_NAME));
    }

    /**
     * Finds the settings directory either via system overrides or defaults to {@code ~/.tetrisjfx}.
     */
    private static Path resolveBaseDirectory() {
        String overrideDir = System.getProperty(SETTINGS_DIR_PROPERTY);
        if (overrideDir != null && !overrideDir.isBlank()) {
            return Paths.get(overrideDir.trim()).toAbsolutePath().normalize();
        }
        String dataDir = System.getProperty(DATA_DIR_PROPERTY);
        if (dataDir != null && !dataDir.isBlank()) {
            return Paths.get(dataDir.trim()).toAbsolutePath().normalize();
        }
        String userHome = System.getProperty("user.home", ".");
        return Paths.get(userHome, DEFAULT_DIRECTORY).toAbsolutePath().normalize();
    }

    private static final class Location {
        private final Path baseDir;
        private final Path file;

        private Location(Path baseDir, Path file) {
            this.baseDir = (baseDir == null ? Paths.get(".") : baseDir).toAbsolutePath().normalize();
            if (file == null) {
                this.file = this.baseDir.resolve(FILE_NAME);
            } else {
                this.file = file.toAbsolutePath().normalize();
            }
        }
    }
}
