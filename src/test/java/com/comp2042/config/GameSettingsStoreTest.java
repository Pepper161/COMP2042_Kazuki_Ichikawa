package com.comp2042.config;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameSettingsStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void loadReturnsDefaultsWhenFileMissing() {
        GameSettingsStore store = new GameSettingsStore(tempDir);
        GameSettings settings = store.load();
        assertNotNull(settings);
        assertEquals(GameSettings.defaultSettings().getDasDelayMs(), settings.getDasDelayMs());
    }

    @Test
    void saveAndReloadRoundTripsValues() {
        GameSettingsStore store = new GameSettingsStore(tempDir);
        GameSettings custom = GameSettings.builder()
                .setDasDelayMs(90)
                .setArrIntervalMs(10)
                .setSoftDropMultiplier(12.5)
                .setKey(GameSettings.Action.MOVE_LEFT, KeyCode.A)
                .setKey(GameSettings.Action.MOVE_RIGHT, KeyCode.D)
                .setKey(GameSettings.Action.SOFT_DROP, KeyCode.S)
                .setKey(GameSettings.Action.HARD_DROP, KeyCode.SPACE)
                .setKey(GameSettings.Action.ROTATE_CW, KeyCode.E)
                .setKey(GameSettings.Action.ROTATE_CCW, KeyCode.Q)
                .setKey(GameSettings.Action.NEW_GAME, KeyCode.P)
                .build();
        store.save(custom);
        GameSettings reloaded = store.load();
        assertEquals(custom.getDasDelayMs(), reloaded.getDasDelayMs());
        assertEquals(custom.getArrIntervalMs(), reloaded.getArrIntervalMs());
        assertEquals(custom.getSoftDropMultiplier(), reloaded.getSoftDropMultiplier(), 0.0001);
        assertEquals(custom.getKey(GameSettings.Action.MOVE_LEFT), reloaded.getKey(GameSettings.Action.MOVE_LEFT));
        assertEquals(custom.getKey(GameSettings.Action.NEW_GAME), reloaded.getKey(GameSettings.Action.NEW_GAME));
    }
}
