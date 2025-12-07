package com.comp2042.audio;

import com.comp2042.config.ResourceManager;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.Optional;

/**
 * Singleton helper that loops menu / gameplay music, exposes quick FX triggers, and gracefully handles missing assets.
 * All audio retrieval happens lazily, so missing WAV files or codec issues simply disable the respective sound instead
 * of crashing the UI. Calls are marshalled to the JavaFX thread to keep Media APIs happy.
 */
public final class BackgroundMusicManager {

    public enum Mode {
        MENU,
        GAME
    }

    private static final BackgroundMusicManager INSTANCE = new BackgroundMusicManager();
    private static final double DEFAULT_MASTER_VOLUME = 0.35;
    private static final double GAME_OVER_VOLUME_MULTIPLIER = 1.2;
    private static final double LINE_CLEAR_VOLUME_MULTIPLIER = 0.8;

    private final AudioClip menuClip;
    private final AudioClip gameClip;
    private final AudioClip gameOverClip;
    private final AudioClip lineClearClip;
    private volatile AudioClip currentClip;
    private Mode currentMode = Mode.MENU;
    private volatile boolean backgroundEnabled = true;
    private volatile double masterVolume = DEFAULT_MASTER_VOLUME;

    private BackgroundMusicManager() {
        menuClip = createLoopingClip(ResourceManager.Asset.AUDIO_MENU_THEME);
        gameClip = createLoopingClip(ResourceManager.Asset.AUDIO_GAME_THEME);
        gameOverClip = createEffectClip(ResourceManager.Asset.AUDIO_GAME_OVER, GAME_OVER_VOLUME_MULTIPLIER);
        lineClearClip = createEffectClip(ResourceManager.Asset.AUDIO_LINE_CLEAR, LINE_CLEAR_VOLUME_MULTIPLIER);
    }

    public static BackgroundMusicManager getInstance() {
        return INSTANCE;
    }

    /**
     * Enables or disables looping BGM. Disabling will stop the active clip immediately.
     *
     * @param enabled whether BGM should be audible
     */
    public void setEnabled(boolean enabled) {
        boolean wasEnabled = this.backgroundEnabled;
        this.backgroundEnabled = enabled;
        if (!enabled) {
            stopCurrent();
        } else if (!wasEnabled) {
            resumeCurrentMode();
        }
    }

    /**
     * Switches to the menu theme (looping).
     */
    public void playMenuTheme() {
        currentMode = Mode.MENU;
        playClip(menuClip);
    }

    /**
     * Switches to the gameplay loop.
     */
    public void playGameTheme() {
        currentMode = Mode.GAME;
        playClip(gameClip);
    }

    /**
     * Plays the one-shot game over jingle if available.
     */
    public void playGameOverJingle() {
        playOneShot(gameOverClip, "game over jingle");
    }

    /**
     * Plays the one-shot line clear sound if available.
     */
    public void playLineClear() {
        playOneShot(lineClearClip, "line clear sound");
    }

    /**
     * Updates the global master volume (0.0 - 1.0) and reapplies it to the cached clips.
     *
     * @param volume normalized volume
     */
    public void setMasterVolume(double volume) {
        masterVolume = Math.max(0.0, Math.min(1.0, volume));
        applyVolume(menuClip);
        applyVolume(gameClip);
        applyVolume(gameOverClip, GAME_OVER_VOLUME_MULTIPLIER);
        applyVolume(lineClearClip, LINE_CLEAR_VOLUME_MULTIPLIER);
    }

    /**
     * Stops the currently playing background loop, if any.
     */
    public void stopBackgroundMusic() {
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
        if (!backgroundEnabled || clip == null) {
            return;
        }
        runOnFxThread(() -> {
            try {
                AudioClip activeClip = currentClip;
                if (activeClip != null && activeClip != clip) {
                    activeClip.stop();
                }
                currentClip = clip;
                if (!clip.isPlaying()) {
                    clip.play();
                }
            } catch (Exception e) {
                System.err.println("[Audio] Failed to switch BGM: " + e.getMessage());
            }
        });
    }

    private void playOneShot(AudioClip clip, String clipLabel) {
        if (clip == null) {
            return;
        }
        runOnFxThread(() -> {
            try {
                clip.stop();
                clip.play();
            } catch (Exception e) {
                System.err.println("[Audio] Failed to play " + clipLabel + ": " + e.getMessage());
            }
        });
    }

    private void stopCurrent() {
        AudioClip clip = currentClip;
        if (clip == null) {
            return;
        }
        runOnFxThread(() -> {
            try {
                clip.stop();
            } catch (Exception e) {
                System.err.println("[Audio] Failed to stop BGM: " + e.getMessage());
            } finally {
                if (currentClip == clip) {
                    currentClip = null;
                }
            }
        });
    }

    private AudioClip createLoopingClip(ResourceManager.Asset asset) {
        return createClip(asset, AudioClip.INDEFINITE, 1.0);
    }

    private AudioClip createEffectClip(ResourceManager.Asset asset, double volumeMultiplier) {
        return createClip(asset, 1, volumeMultiplier);
    }

    private AudioClip createClip(ResourceManager.Asset asset, int cycles, double multiplier) {
        Optional<URL> resource = ResourceManager.findUrl(asset);
        if (resource.isEmpty()) {
            System.err.println("[Audio] Missing resource: " + asset.path());
            return null;
        }
        try {
            AudioClip clip = new AudioClip(resource.get().toExternalForm());
            clip.setCycleCount(cycles);
            applyVolume(clip, multiplier);
            return clip;
        } catch (RuntimeException ex) {
            System.err.println("[Audio] Failed to load clip " + asset.path() + ": " + ex.getMessage());
            return null;
        }
    }

    private void applyVolume(AudioClip clip) {
        applyVolume(clip, 1.0);
    }

    private void applyVolume(AudioClip clip, double multiplier) {
        if (clip == null) {
            return;
        }
        double clamped = Math.max(0.0, Math.min(1.0, masterVolume * multiplier));
        try {
            clip.setVolume(clamped);
        } catch (Exception ex) {
            System.err.println("[Audio] Failed to apply volume: " + ex.getMessage());
        }
    }

    private void runOnFxThread(Runnable action) {
        if (action == null) {
            return;
        }
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
