package com.comp2042.ui;

import com.comp2042.game.Score;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Stylised HUD panel that mirrors the "run telemetry" card in the HTML reference.
 * Handles scoreboard bindings while exposing extra slots for level/mode badges.
 */
public class HudPanel extends VBox {

    private final Label scoreValue;
    private final Label comboValue;
    private final Label b2bValue;
    private final Label levelValue;
    private final Label modeValue;
    private final VBox statsContainer = new VBox(12);

    public HudPanel() {
        setSpacing(16);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("hud-panel");

        Label kicker = new Label("RUN TELEMETRY");
        kicker.getStyleClass().add("hud-panel-kicker");

        Label title = new Label("Live Stack Data");
        title.getStyleClass().add("hud-panel-title");

        statsContainer.getStyleClass().add("hud-stats");

        scoreValue = addStat("Score", "0", true);
        comboValue = addStat("Combo", "0", false);
        b2bValue = addStat("Back-to-Back", "--", false);
        levelValue = addStat("Level", "1", false);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        modeValue = new Label("Mode: Endless");
        modeValue.getStyleClass().add("hud-mode-status");
        modeValue.setWrapText(true);

        Label hint = new Label("ESC pauses · F1 opens help");
        hint.getStyleClass().add("hud-footer-hint");

        getChildren().addAll(kicker, title, statsContainer, spacer, modeValue, hint);
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
        block.getStyleClass().addAll("hud-panel-row", "hud-stat-block");
        statsContainer.getChildren().add(block);
        return value;
    }
}
