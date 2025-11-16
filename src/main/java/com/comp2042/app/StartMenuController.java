package com.comp2042.app;

import com.comp2042.game.GameController;
import com.comp2042.game.GameState;
import com.comp2042.ui.GuiController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Controller for the start menu. Handles navigation into the gameplay scene.
 */
public class StartMenuController {

    public static final double WINDOW_WIDTH = 300;
    public static final double WINDOW_HEIGHT = 510;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void onStart(ActionEvent event) {
        ensurePrimaryStageBound();
        try {
            URL layoutUrl = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader loader = new FXMLLoader(layoutUrl);
            Parent root = loader.load();
            GuiController guiController = loader.getController();
            guiController.setPrimaryStage(primaryStage);
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.show();
            new GameController(guiController);
            guiController.setGameState(GameState.PLAYING);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load game layout.", ex);
        }
    }

    @FXML
    private void onExit(ActionEvent event) {
        Platform.exit();
    }

    private void ensurePrimaryStageBound() {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been injected into StartMenuController.");
        }
    }
}
