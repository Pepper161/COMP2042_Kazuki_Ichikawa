package com.comp2042.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Overlay shown when the game reaches a GAME OVER state.
 */
public class GameOverPanel extends BorderPane {

    private Runnable onRestart = () -> {};
    private Runnable onExit = () -> {};
    private Runnable onMainMenu = () -> {};

    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");

        Button restartButton = new Button("Restart");
        restartButton.getStyleClass().add("ipad-dark-grey");
        restartButton.setOnAction(event -> onRestart.run());

        Button menuButton = new Button("Main Menu");
        menuButton.getStyleClass().add("ipad-dark-grey");
        menuButton.setOnAction(event -> onMainMenu.run());

        Button exitButton = new Button("Exit");
        exitButton.getStyleClass().add("ipad-dark-grey");
        exitButton.setOnAction(event -> onExit.run());

        VBox container = new VBox(18, gameOverLabel, restartButton, menuButton, exitButton);
        container.setAlignment(Pos.CENTER);
        container.setFillWidth(false);

        setCenter(container);
        getStyleClass().add("game-over-panel");
    }

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart != null ? onRestart : () -> {};
    }

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit != null ? onExit : () -> {};
    }

    public void setOnMainMenu(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu != null ? onMainMenu : () -> {};
    }
}
