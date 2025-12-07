package com.comp2042.game.stats;

import com.comp2042.game.GameConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighScoreServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void recordScoreCapsEntriesPerMode() {
        Path file = tempDir.resolve("scores.dat");
        HighScoreService service = new HighScoreService(file);
        for (int i = 0; i < 15; i++) {
            HighScoreEntry entry = HighScoreEntry.recreate(2000 - i, "Endless", i, i);
            service.recordScore(entry);
        }
        List<HighScoreEntry> endless = service.fetchLeaderboardForMode(GameConfig.GameMode.ENDLESS);
        assertEquals(10, endless.size());
        assertTrue(endless.get(0).getScore() > endless.get(endless.size() - 1).getScore());
    }

    @Test
    void clearDeletesStorageFile() throws Exception {
        Path file = tempDir.resolve("scores.dat");
        HighScoreService service = new HighScoreService(file);
        service.recordScore(500, "Endless", Duration.ofSeconds(30));
        assertTrue(Files.exists(file));
        service.clear();
        assertFalse(Files.exists(file));
    }
}
