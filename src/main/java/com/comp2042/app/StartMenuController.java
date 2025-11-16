package com.comp2042.app;

import com.comp2042.config.GameSettings;
import com.comp2042.config.GameSettingsStore;
import com.comp2042.game.GameConfig;
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
import javafx.stage.Modality;

/**
 * Controller for the start menu. Handles navigation into the gameplay scene.
 */
public class StartMenuController {

    public static final double WINDOW_WIDTH = 300;
    public static final double WINDOW_HEIGHT = 510;

    private Stage primaryStage;
    private GameConfig gameConfig = GameConfig.defaultConfig();
    private final GameSettingsStore settingsStore = new GameSettingsStore();
    private GameSettings gameSettings = settingsStore.load();

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
            new GameController(guiController, gameConfig, gameSettings);
            guiController.setGameState(GameState.PLAYING);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load game layout.", ex);
        }
    }

    @FXML
    private void onExit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void onSettings(ActionEvent event) {
        ensurePrimaryStageBound();
        try {
            URL settingsUrl = getClass().getClassLoader().getResource("SettingsDialog.fxml");
            FXMLLoader loader = new FXMLLoader(settingsUrl);
            Parent root = loader.load();
            SettingsController controller = loader.getController();
            Stage dialog = new Stage();
            dialog.setTitle("Settings");
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.WINDOW_MODAL);
            controller.setDialogStage(dialog);
            controller.setInitialSettings(gameSettings);
            Scene scene = new Scene(root, 420, 520);
            dialog.setScene(scene);
            dialog.showAndWait();
            controller.getResult().ifPresent(result -> gameSettings = result);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load settings dialog.", ex);
        }
    }

    private void ensurePrimaryStageBound() {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been injected into StartMenuController.");
        }
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig != null ? gameConfig : GameConfig.defaultConfig();
    }
}
