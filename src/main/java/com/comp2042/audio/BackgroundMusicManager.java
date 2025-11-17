package com.comp2042.audio;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * Singleton helper that loops menu / gameplay music and allows toggling BGM globally.
 */
public final class BackgroundMusicManager {

    public enum Mode {
        MENU,
        GAME
    }

    private static final BackgroundMusicManager INSTANCE = new BackgroundMusicManager();

    private final MediaPlayer menuPlayer;
    private final MediaPlayer gamePlayer;
    private MediaPlayer currentPlayer;
    private Mode currentMode = Mode.MENU;
    private boolean enabled = true;

    private BackgroundMusicManager() {
        menuPlayer = createLoopingPlayer("audio/menu_theme.wav");
        gamePlayer = createLoopingPlayer("audio/game_theme.wav");
    }

    public static BackgroundMusicManager getInstance() {
        return INSTANCE;
    }

    public void setEnabled(boolean enabled) {
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;
        if (!enabled) {
            stopCurrent();
        } else if (!wasEnabled) {
            resumeCurrentMode();
        }
    }

    public void playMenuTheme() {
        currentMode = Mode.MENU;
        playPlayer(menuPlayer);
    }

    public void playGameTheme() {
        currentMode = Mode.GAME;
        playPlayer(gamePlayer);
    }

    public void stopAll() {
        stopCurrent();
    }

    private void resumeCurrentMode() {
        switch (currentMode) {
            case GAME -> playPlayer(gamePlayer);
            case MENU -> playPlayer(menuPlayer);
            default -> {
            }
        }
    }

    private void playPlayer(MediaPlayer player) {
        if (!enabled || player == null) {
            return;
        }
        Runnable action = () -> {
            if (currentPlayer != null && currentPlayer != player) {
                currentPlayer.stop();
            }
            currentPlayer = player;
            currentPlayer.stop();
            currentPlayer.play();
        };
        runOnFxThread(action);
    }

    private void stopCurrent() {
        if (currentPlayer == null) {
            return;
        }
        runOnFxThread(() -> currentPlayer.stop());
        currentPlayer = null;
    }

    private MediaPlayer createLoopingPlayer(String resourcePath) {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            System.err.println("[Audio] Missing resource: " + resourcePath);
            return null;
        }
        Media media = new Media(resource.toExternalForm());
        MediaPlayer player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setVolume(0.35);
        return player;
    }

    private void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
