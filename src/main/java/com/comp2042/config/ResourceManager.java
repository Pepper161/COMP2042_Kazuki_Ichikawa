package com.comp2042.config;

import java.io.InputStream;
import java.net.URL;

/**
 * Central registry for application resources (FXML, audio, fonts, docs).
 * All lookup helpers validate existence eagerly so missing assets are detected early.
 */
public final class ResourceManager {

    public enum Asset {
        START_MENU_FXML("StartMenu.fxml"),
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
            this.path = path;
        }

        public String path() {
            return path;
        }
    }

    private static final ClassLoader LOADER = ResourceManager.class.getClassLoader();

    private ResourceManager() {
    }

    public static URL getUrl(Asset asset) {
        URL resource = LOADER.getResource(asset.path());
        if (resource == null) {
            throw new IllegalStateException("Missing resource: " + asset.path());
        }
        return resource;
    }

    public static String getExternalForm(Asset asset) {
        return getUrl(asset).toExternalForm();
    }

    public static InputStream openStream(Asset asset) {
        InputStream inputStream = LOADER.getResourceAsStream(asset.path());
        if (inputStream == null) {
            throw new IllegalStateException("Missing resource: " + asset.path());
        }
        return inputStream;
    }
}
