package com.comp2042.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelProgressionTest {

    @Test
    void resetRestoresLevelOneAndTenLineThreshold() {
        LevelProgression progression = new LevelProgression();

        LevelProgression.LevelState state = progression.reset();

        assertEquals(1, state.level());
        assertEquals(LevelProgression.DEFAULT_LINES_PER_LEVEL, state.linesUntilNextLevel());
        assertEquals(LevelProgression.DEFAULT_BASE_GRAVITY_MS, state.gravityIntervalMs());
        assertFalse(state.leveledUp());
    }

    @Test
    void linesOnlyAdvanceWhenThresholdReached() {
        LevelProgression progression = new LevelProgression();
        progression.reset();

        LevelProgression.LevelState afterNine = progression.handleLinesCleared(9);
        assertEquals(1, afterNine.level());
        assertEquals(1, afterNine.linesUntilNextLevel());
        assertFalse(afterNine.leveledUp());

        LevelProgression.LevelState afterFull = progression.handleLinesCleared(1);
        assertEquals(2, afterFull.level());
        assertEquals(LevelProgression.DEFAULT_LINES_PER_LEVEL, afterFull.linesUntilNextLevel());
        assertTrue(afterFull.leveledUp());
    }

    @Test
    void gravityClampsToMinimumBeyondTable() {
        LevelProgression progression = new LevelProgression();
        progression.reset();

        // Apply enough lines to move well past the default table length.
        LevelProgression.LevelState state = null;
        for (int i = 0; i < 200; i++) {
            state = progression.handleLinesCleared(LevelProgression.DEFAULT_LINES_PER_LEVEL);
        }

        assertTrue(state.level() > 0);
        assertEquals(LevelProgression.DEFAULT_MIN_GRAVITY_MS, state.gravityIntervalMs());
    }
}
