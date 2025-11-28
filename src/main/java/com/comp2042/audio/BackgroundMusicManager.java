package com.comp2042.audio;

import com.comp2042.config.ResourceManager;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;

import java.net.URL;

/**
 * Singleton helper that loops menu / gameplay music and allows toggling BGM
 * globally.
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
        menuClip = createLoopingClip(ResourceManager.Asset.AUDIO_MENU_THEME);
        gameClip = createLoopingClip(ResourceManager.Asset.AUDIO_GAME_THEME);
        gameOverClip = createSingleShotClip(ResourceManager.Asset.AUDIO_GAME_OVER, 1.2);
        lineClearClip = createSingleShotClip(ResourceManager.Asset.AUDIO_LINE_CLEAR, 0.8);
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
            try {
                gameOverClip.stop();
                gameOverClip.play();
            } catch (Exception e) {
                System.err.println("[Audio] Failed to play game over jingle: " + e.getMessage());
            }
        });
    }

    public void playLineClear() {
        if (lineClearClip == null) {
            return;
        }
        runOnFxThread(() -> {
            try {
                lineClearClip.stop();
                lineClearClip.play();
            } catch (Exception e) {
                System.err.println("[Audio] Failed to play line clear sound: " + e.getMessage());
            }
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
            try {
                if (currentClip != null && currentClip != clip) {
                    currentClip.stop();
                }
                currentClip = clip;
                if (currentClip != null) {
                    currentClip.stop();
                    currentClip.play();
                }
            } catch (Exception e) {
                System.err.println("[Audio] Failed to switch BGM: " + e.getMessage());
            }
        });
    }

    public void stopBackgroundMusic() {
        stopCurrent();
    }

    private void stopCurrent() {
        if (currentClip == null) {
            return;
        }
        runOnFxThread(() -> {
            try {
                if (currentClip != null) {
                    currentClip.stop();
                }
            } catch (Exception e) {
                System.err.println("[Audio] Failed to stop BGM: " + e.getMessage());
            }
        });
        currentClip = null;
    }

    private AudioClip createLoopingClip(ResourceManager.Asset asset) {
        URL resource = ResourceManager.getUrl(asset);
        AudioClip clip = new AudioClip(resource.toExternalForm());
        clip.setCycleCount(AudioClip.INDEFINITE);
        applyVolume(clip);
        return clip;
    }

    private AudioClip createSingleShotClip(ResourceManager.Asset asset, double volumeMultiplier) {
        URL resource = ResourceManager.getUrl(asset);
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
