package com.comp2042.game;

/**
 * Immutable value object describing the outcome of a line clear.
 */
public final class LineClearStats {

    private final LineClearType clearType;
    private final int linesCleared;

    public LineClearStats(LineClearType clearType, int linesCleared) {
        this.clearType = clearType;
        this.linesCleared = linesCleared;
    }

    public LineClearType getClearType() {
        return clearType;
    }

    public int getLinesCleared() {
        return linesCleared;
    }
}
