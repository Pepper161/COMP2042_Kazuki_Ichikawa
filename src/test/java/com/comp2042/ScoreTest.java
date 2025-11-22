package com.comp2042;

import com.comp2042.game.LineClearStats;
import com.comp2042.game.LineClearType;
import com.comp2042.game.Score;
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

    @Test
    void backToBackAwardsFiftyPercentBonus() {
        Score score = new Score();
        score.handleLineClear(new LineClearStats(LineClearType.TETRIS, 4));
        assertEquals(800, score.scoreProperty().get());

        score.handleLineClear(new LineClearStats(LineClearType.TETRIS, 4));

        assertEquals(2050, score.scoreProperty().get(),
                "Second Tetris should include B2B bonus (+400) and combo bonus (+50)");
    }

    @Test
    void resetClearsAllState() {
        Score score = new Score();
        score.handleLineClear(new LineClearStats(LineClearType.TRIPLE, 3));
        score.handleLineClear(new LineClearStats(LineClearType.DOUBLE, 2));

        score.reset();

        assertEquals(0, score.scoreProperty().get());
        assertEquals(0, score.comboProperty().get());
        assertEquals(0, score.backToBackProperty().get());
    }
}
