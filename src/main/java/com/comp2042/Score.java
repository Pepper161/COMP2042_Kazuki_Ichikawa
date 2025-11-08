package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty combo = new SimpleIntegerProperty(0);
    private final IntegerProperty backToBack = new SimpleIntegerProperty(0);
    private boolean lastClearEligibleForB2B = false;

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

    public void handleLineClear(int linesRemoved) {
        if (linesRemoved <= 0) {
            combo.set(0);
            backToBack.set(0);
            lastClearEligibleForB2B = false;
            return;
        }
        combo.set(combo.get() + 1);
        boolean b2bCandidate = linesRemoved >= 4;
        if (b2bCandidate) {
            if (lastClearEligibleForB2B) {
                backToBack.set(backToBack.get() + 1);
            } else {
                backToBack.set(1);
            }
            lastClearEligibleForB2B = true;
        } else {
            backToBack.set(0);
            lastClearEligibleForB2B = false;
        }
    }

    public void reset() {
        score.setValue(0);
        combo.set(0);
        backToBack.set(0);
        lastClearEligibleForB2B = false;
    }
}
