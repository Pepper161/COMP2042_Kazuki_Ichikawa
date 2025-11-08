package com.comp2042;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreTest {

    @Test
    void comboIncrementsOnConsecutiveClears() {
        Score score = new Score();
        score.handleLineClear(1);
        score.handleLineClear(2);
        assertEquals(2, score.comboProperty().get());
    }

    @Test
    void comboResetsWhenNoLinesCleared() {
        Score score = new Score();
        score.handleLineClear(1);
        score.handleLineClear(0);
        assertEquals(0, score.comboProperty().get());
    }

    @Test
    void backToBackCountsOnlyQualifyingClears() {
        Score score = new Score();
        score.handleLineClear(4);
        assertEquals(1, score.backToBackProperty().get());
        score.handleLineClear(4);
        assertEquals(2, score.backToBackProperty().get());
        score.handleLineClear(2);
        assertEquals(0, score.backToBackProperty().get());
    }
}

