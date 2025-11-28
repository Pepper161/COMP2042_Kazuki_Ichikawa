package com.comp2042.ui;

import com.comp2042.game.Score;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Simple HUD panel showing score, combo count, and back-to-back streak.
 */
public class HudPanel extends VBox {

    private final Label scoreValue;
    private final Label comboValue;
    private final Label b2bValue;
    private final Label levelValue;
    private final Label modeValue;

    public HudPanel() {
        setSpacing(12);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("hud-panel");

        scoreValue = addStat("Score", "0", true);
        comboValue = addStat("Combo", "0", false);
        b2bValue = addStat("Back-to-Back", "--", false);
        levelValue = addStat("Level", "1", false);
        modeValue = addStat("Mode", "Endless", false);
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
        scoreValue.textProperty().bind(scoreProperty.asString("%,d"));
    }

    private void bindComboProperty(IntegerProperty comboProperty) {
        comboValue.textProperty().bind(comboProperty.asString("%d"));
    }

    private void bindB2BProperty(IntegerProperty b2bProperty) {
        b2bValue.textProperty().bind(Bindings.createStringBinding(
                () -> b2bProperty.get() > 0
                        ? String.format("%d", b2bProperty.get())
                        : "--",
                b2bProperty));
    }

    public void setLevel(int level) {
        levelValue.setText(String.valueOf(level));
    }

    public void setModeStatus(String status) {
        modeValue.setText(status != null ? status : "");
    }

    private Label addStat(String title, String initialValue, boolean emphasize) {
        Label header = new Label(title.toUpperCase());
        header.getStyleClass().add("hud-label");

        Label value = new Label(initialValue);
        value.getStyleClass().add("hud-value");
        if (emphasize) {
            value.getStyleClass().add("hud-value-lg");
        }

        VBox block = new VBox(4, header, value);
        block.getStyleClass().add("hud-panel-row");
        getChildren().add(block);
        return value;
    }
}
