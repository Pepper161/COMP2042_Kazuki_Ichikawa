package com.comp2042.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 * Central registry for application resources (FXML, fonts, markdown, audio).
 * <p>
 * Callers can request strongly typed {@link Asset}s or look up arbitrary classpath paths. Each accessor provides both
 * a safe optional variant (for optional audio/markdown) and a fail-fast variant (for mandatory layouts).
 * </p>
 */
public final class ResourceManager {

    /**
     * Known assets bundled inside {@code src/main/resources} and resolved relative to the classpath root.
     */
    public enum Asset {
        START_MENU_FXML("StartMenu.fxml"),
        MODE_SELECT_FXML("ModeSelect.fxml"),
        GAME_LAYOUT_FXML("gameLayout.fxml"),
        SETTINGS_DIALOG_FXML("SettingsDialog.fxml"),
        HELP_DIALOG_FXML("HelpDialog.fxml"),
        DIGITAL_FONT("digital.ttf"),
        HELP_MARKDOWN("help/help-content.md"),
        AUDIO_MENU_THEME("audio/menu_theme.wav"),
        AUDIO_GAME_THEME("audio/game_theme.wav"),
        AUDIO_GAME_OVER("audio/game_over.wav"),
        AUDIO_LINE_CLEAR("audio/line_clear.wav");

        private final String path;

        Asset(String path) {
            this.path = normalize(path);
        }

        /**
         * @return normalized resource path relative to the classpath root
         */
        public String path() {
            return path;
        }
    }

    private static final ClassLoader LOADER = ResourceManager.class.getClassLoader();

    private ResourceManager() {
    }

    /**
     * Attempts to resolve the raw resource path (useful for assets defined outside {@link Asset}).
     *
     * @param relativePath path relative to {@code src/main/resources}
     * @return optional URL if the resource exists
     */
    public static Optional<URL> findUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(LOADER.getResource(normalize(relativePath)));
    }

    /**
     * Returns an optional URL for the provided asset without throwing if the asset is missing.
     *
     * @param asset logical asset descriptor
     * @return optional URL that resolves to the resource
     */
    public static Optional<URL> findUrl(Asset asset) {
        Objects.requireNonNull(asset, "asset");
        return findUrl(asset.path());
    }

    /**
     * Returns a URL for the resource or fails fast with a descriptive exception if absent.
     *
     * @param asset logical asset descriptor
     * @return URL pointing to the asset
     */
    public static URL getUrl(Asset asset) {
        return findUrl(asset)
                .orElseThrow(() -> new IllegalStateException("Missing resource: " + asset.path()));
    }

    /**
     * Returns the external-form string for use with APIs that require textual URLs (e.g. Font loader).
     *
     * @param asset logical asset descriptor
     * @return external-form URL string
     */
    public static String getExternalForm(Asset asset) {
        return getUrl(asset).toExternalForm();
    }

    /**
     * Opens the resource as an {@link InputStream}, failing fast if the asset cannot be located.
     *
     * @param asset logical asset descriptor
     * @return readable input stream
     */
    public static InputStream openStream(Asset asset) {
        return openOptionalStream(asset)
                .orElseThrow(() -> new IllegalStateException("Missing resource: " + asset.path()));
    }

    /**
     * Opens the resource as an {@link InputStream} if available.
     *
     * @param asset logical asset descriptor
     * @return optional input stream which must be closed by the caller
     */
    public static Optional<InputStream> openOptionalStream(Asset asset) {
        Objects.requireNonNull(asset, "asset");
        return Optional.ofNullable(LOADER.getResourceAsStream(asset.path()));
    }

    /**
     * Reads the entire resource into UTF-8 text which is handy for Markdown or JSON fixtures.
     *
     * @param asset logical asset descriptor
     * @return optional text contents
     */
    public static Optional<String> readText(Asset asset) {
        Optional<InputStream> optional = openOptionalStream(asset);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream in = optional.get()) {
            return Optional.of(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            System.err.println("[ResourceManager] Failed to read " + asset.path() + ": " + ex.getMessage());
            return Optional.empty();
        }
    }

    private static String normalize(String path) {
        String trimmed = path == null ? "" : path.strip();
        if (trimmed.startsWith("/")) {
            return trimmed.substring(1);
        }
        return trimmed;
    }
}
