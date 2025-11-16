package com.comp2042.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Tracks score, combo, and back-to-back counters using JavaFX properties for UI binding.
 */
public final class Score {

    private static final int COMBO_STEP = 50;

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
            return;
        }

        combo.set(combo.get() + 1);
        int base = stats.getClearType().getBaseScore();
        int scoreWithB2B = applyBackToBack(base, stats.getClearType().isBackToBackEligible());
        int comboBonus = combo.get() > 1 ? (combo.get() - 1) * COMBO_STEP : 0;
        add(scoreWithB2B + comboBonus);
    }

    public void reset() {
        score.setValue(0);
        combo.set(0);
        backToBack.set(0);
        backToBackActive = false;
    }

    private int applyBackToBack(int base, boolean eligible) {
        if (!eligible) {
            backToBackActive = false;
            backToBack.set(0);
            return base;
        }
        if (backToBackActive) {
            backToBack.set(backToBack.get() + 1);
            int bonus = Math.round(base * 0.5f);
            return base + bonus;
        } else {
            backToBackActive = true;
            backToBack.set(1);
            return base;
        }
    }
}
