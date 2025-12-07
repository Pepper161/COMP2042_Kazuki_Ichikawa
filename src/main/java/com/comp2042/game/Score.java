package com.comp2042.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Tracks score, combo, and back-to-back counters using JavaFX properties for UI binding.
 */
public final class Score {

    private static final int BASE_LINE_SCORE = 50;

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty combo = new SimpleIntegerProperty(0);
    private final IntegerProperty backToBack = new SimpleIntegerProperty(0);
    private boolean backToBackActive = false;

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty comboProperty() {
        return combo;
    }

    public IntegerProperty backToBackProperty() {
        return backToBack;
    }

    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    public void handleLineClear(LineClearStats stats) {
        if (stats == null || stats.getLinesCleared() <= 0 || stats.getClearType() == LineClearType.NONE) {
            combo.set(0);
            resetBackToBack();
            return;
        }

        combo.set(combo.get() + 1);
        updateBackToBack(stats.getClearType());
        add(calculateFlatScore(stats.getLinesCleared()));
    }

    public void reset() {
        score.setValue(0);
        combo.set(0);
        backToBack.set(0);
        backToBackActive = false;
    }

    private void resetBackToBack() {
        backToBackActive = false;
        backToBack.set(0);
    }

    private void updateBackToBack(LineClearType clearType) {
        if (clearType == null || !clearType.isBackToBackEligible()) {
            resetBackToBack();
            return;
        }
        if (backToBackActive) {
            backToBack.set(backToBack.get() + 1);
        } else {
            backToBackActive = true;
            backToBack.set(1);
        }
    }

    private int calculateFlatScore(int lines) {
        return BASE_LINE_SCORE * lines * lines;
    }
}
