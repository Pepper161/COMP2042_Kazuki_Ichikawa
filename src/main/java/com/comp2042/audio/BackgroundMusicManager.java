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
    private final AudioClip gameOverClip;
    private final AudioClip lineClearClip;
    private AudioClip currentClip;
    private Mode currentMode = Mode.MENU;
    private boolean backgroundEnabled = true;
    private double masterVolume = 0.35;

    private BackgroundMusicManager() {
        menuClip = createLoopingClip("audio/menu_theme.wav");
        gameClip = createLoopingClip("audio/game_theme.wav");
        gameOverClip = createSingleShotClip("audio/game_over.wav", 1.2);
        lineClearClip = createSingleShotClip("audio/line_clear.wav", 0.8);
    }

    public static BackgroundMusicManager getInstance() {
        return INSTANCE;
    }

    public void setEnabled(boolean enabled) {
        boolean wasEnabled = this.backgroundEnabled;
        this.backgroundEnabled = enabled;
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

    public void playGameOverJingle() {
        if (gameOverClip == null) {
            return;
        }
        runOnFxThread(() -> {
            gameOverClip.stop();
            gameOverClip.play();
        });
    }

    public void playLineClear() {
        if (lineClearClip == null) {
            return;
        }
        runOnFxThread(() -> {
            lineClearClip.stop();
            lineClearClip.play();
        });
    }

    public void setMasterVolume(double volume) {
        masterVolume = Math.max(0.0, Math.min(1.0, volume));
        applyVolume(menuClip);
        applyVolume(gameClip);
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
        if (!backgroundEnabled || clip == null) {
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

    public void stopBackgroundMusic() {
        stopCurrent();
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
        applyVolume(clip);
        return clip;
    }

    private AudioClip createSingleShotClip(String resourcePath, double volumeMultiplier) {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            System.err.println("[Audio] Missing resource: " + resourcePath);
            return null;
        }
        AudioClip clip = new AudioClip(resource.toExternalForm());
        clip.setCycleCount(1);
        clip.setVolume(Math.max(0.0, Math.min(1.0, masterVolume * volumeMultiplier)));
        return clip;
    }

    private void applyVolume(AudioClip clip) {
        if (clip != null) {
            clip.setVolume(masterVolume);
        }
    }

    private void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
