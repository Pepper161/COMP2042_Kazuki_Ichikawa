package com.comp2042;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

/**
 * Simple HUD panel showing score, combo count, and back-to-back streak.
 */
public class HudPanel extends VBox {

    private final Label scoreLabel = new Label("Score: 0");
    private final Label comboLabel = new Label("Combo: 0");
    private final Label b2bLabel = new Label("B2B: --");

    public HudPanel() {
        setSpacing(6);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("hud-panel");
        scoreLabel.getStyleClass().add("hud-label");
        comboLabel.getStyleClass().add("hud-label");
        b2bLabel.getStyleClass().add("hud-label");
        getChildren().addAll(scoreLabel, comboLabel, b2bLabel);
    }

    public void bindScore(Score score) {
        if (score == null) {
            return;
        }
        bindScoreProperty(score.scoreProperty());
        bindComboProperty(score.comboProperty());
        bindB2BProperty(score.backToBackProperty());
    }

    private void bindScoreProperty(IntegerProperty scoreProperty) {
        scoreLabel.textProperty().bind(scoreProperty.asString("Score: %,d"));
    }

    private void bindComboProperty(IntegerProperty comboProperty) {
        comboLabel.textProperty().bind(comboProperty.asString("Combo: %d"));
    }

    private void bindB2BProperty(IntegerProperty b2bProperty) {
        b2bLabel.textProperty().bind(Bindings.createStringBinding(
                () -> b2bProperty.get() > 0
                        ? String.format("B2B: %d", b2bProperty.get())
                        : "B2B: --",
                b2bProperty));
    }
}

