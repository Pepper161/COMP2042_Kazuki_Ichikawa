package com.comp2042.game;

/**
 * Represents the classification of a line clear for scoring purposes.
 */
public enum LineClearType {
    NONE(0),
    SINGLE(100),
    DOUBLE(300),
    TRIPLE(500),
    TETRIS(800),
    T_SPIN_SINGLE(800),
    T_SPIN_DOUBLE(1200),
    T_SPIN_TRIPLE(1600);

    private final int baseScore;

    LineClearType(int baseScore) {
        this.baseScore = baseScore;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public boolean isBackToBackEligible() {
        return this == TETRIS
                || this == T_SPIN_SINGLE
                || this == T_SPIN_DOUBLE
                || this == T_SPIN_TRIPLE;
    }
}
