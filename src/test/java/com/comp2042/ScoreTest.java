package com.comp2042;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreTest {

    @Test
    void comboIncrementsOnConsecutiveClears() {
        Score score = new Score();
        score.handleLineClear(new LineClearStats(LineClearType.SINGLE, 1));
        score.handleLineClear(new LineClearStats(LineClearType.SINGLE, 1));
        assertEquals(2, score.comboProperty().get());
        assertEquals(250, score.scoreProperty().get());
    }

    @Test
    void comboResetsWhenNoLinesCleared() {
        Score score = new Score();
        score.handleLineClear(new LineClearStats(LineClearType.SINGLE, 1));
        score.handleLineClear(new LineClearStats(LineClearType.NONE, 0));
        assertEquals(0, score.comboProperty().get());
    }

    @Test
    void backToBackCountsOnlyQualifyingClears() {
        Score score = new Score();
        score.handleLineClear(new LineClearStats(LineClearType.TETRIS, 4));
        assertEquals(1, score.backToBackProperty().get());
        score.handleLineClear(new LineClearStats(LineClearType.TETRIS, 4));
        assertEquals(2, score.backToBackProperty().get());
        score.handleLineClear(new LineClearStats(LineClearType.DOUBLE, 2));
        assertEquals(0, score.backToBackProperty().get());
    }

    @Test
    void tSpinScoresCorrectBaseValue() {
        Score score = new Score();
        score.handleLineClear(new LineClearStats(LineClearType.T_SPIN_DOUBLE, 2));
        assertEquals(1200, score.scoreProperty().get());
    }
}
}
