package com.comp2042.audio;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;

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

    private final AudioClip menuClip;
    private final AudioClip gameClip;
    private AudioClip currentClip;
    private Mode currentMode = Mode.MENU;
    private boolean enabled = true;

    private BackgroundMusicManager() {
        menuClip = createLoopingClip("audio/menu_theme.wav");
        gameClip = createLoopingClip("audio/game_theme.wav");
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
        playClip(menuClip);
    }

    public void playGameTheme() {
        currentMode = Mode.GAME;
        playClip(gameClip);
    }

    public void stopAll() {
        stopCurrent();
    }

    private void resumeCurrentMode() {
        switch (currentMode) {
            case GAME -> playClip(gameClip);
            case MENU -> playClip(menuClip);
            default -> {
            }
        }
    }

    private void playClip(AudioClip clip) {
        if (!enabled || clip == null) {
            return;
        }
        runOnFxThread(() -> {
            if (currentClip != null && currentClip != clip) {
                currentClip.stop();
            }
            currentClip = clip;
            currentClip.stop();
            currentClip.play();
        });
    }

    private void stopCurrent() {
        if (currentClip == null) {
            return;
        }
        runOnFxThread(() -> currentClip.stop());
        currentClip = null;
    }

    private AudioClip createLoopingClip(String resourcePath) {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            System.err.println("[Audio] Missing resource: " + resourcePath);
            return null;
        }
        AudioClip clip = new AudioClip(resource.toExternalForm());
        clip.setCycleCount(AudioClip.INDEFINITE);
        clip.setVolume(0.35);
        return clip;
    }

    private void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
