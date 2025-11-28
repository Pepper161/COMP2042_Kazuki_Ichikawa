package com.comp2042.app;

import com.comp2042.audio.BackgroundMusicManager;
import com.comp2042.config.GameSettings;
import com.comp2042.config.GameSettingsStore;
import com.comp2042.config.ResourceManager;
import com.comp2042.game.GameConfig;
import com.comp2042.game.GameController;
import com.comp2042.game.GameState;
import com.comp2042.game.stats.HighScoreEntry;
import com.comp2042.game.stats.HighScoreService;
import com.comp2042.help.HelpContentProvider;
import com.comp2042.ui.GuiController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javafx.stage.Modality;

/**
 * Controller for the start menu. Handles navigation into the gameplay scene.
 */
public class StartMenuController {

    public static final double MENU_WINDOW_WIDTH = 420;
    public static final double MENU_WINDOW_HEIGHT = 700;
    public static final double GAME_WINDOW_WIDTH = 540;
    public static final double GAME_WINDOW_HEIGHT = 720;

    private Stage primaryStage;
    private GameConfig gameConfig = GameConfig.defaultConfig();
    private final GameSettingsStore settingsStore = new GameSettingsStore();
    private GameSettings gameSettings = settingsStore.load();
    private final BackgroundMusicManager musicManager = BackgroundMusicManager.getInstance();
    private final HighScoreService highScoreService = new HighScoreService();
    private final HelpContentProvider helpContentProvider = HelpContentProvider.getInstance();

    @FXML
    private VBox leaderboardContainer;

    @FXML
    private Label leaderboardEmptyLabel;

    @FXML
    private Button clearScoresButton;

    @FXML
    public void initialize() {
        refreshLeaderboard();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(MENU_WINDOW_WIDTH);
        primaryStage.setMinHeight(MENU_WINDOW_HEIGHT);
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.setMaxHeight(Double.MAX_VALUE);
        musicManager.setEnabled(gameSettings.isBgmEnabled());
        musicManager.setMasterVolume(gameSettings.getBgmVolume());
        musicManager.playMenuTheme();
    }

    @FXML
    private void onStart(ActionEvent event) {
        GameConfig.GameMode selected = promptModeSelection();
        if (selected == null) {
            return;
        }
        gameConfig = gameConfig.withMode(selected);
        ensurePrimaryStageBound();
        try {
            URL layoutUrl = ResourceManager.getUrl(ResourceManager.Asset.GAME_LAYOUT_FXML);
            FXMLLoader loader = new FXMLLoader(layoutUrl);
            Parent root = loader.load();
            GuiController guiController = loader.getController();
            guiController.setPrimaryStage(primaryStage);
            Scene scene = new Scene(root, GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(GAME_WINDOW_WIDTH);
            primaryStage.setMinHeight(GAME_WINDOW_HEIGHT);
            primaryStage.setMaxWidth(GAME_WINDOW_WIDTH);
            primaryStage.setMaxHeight(GAME_WINDOW_HEIGHT);
            primaryStage.show();
            new GameController(guiController, gameConfig, gameSettings);
            guiController.setGameState(GameState.PLAYING);
            musicManager.playGameTheme();
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
            URL settingsUrl = ResourceManager.getUrl(ResourceManager.Asset.SETTINGS_DIALOG_FXML);
            FXMLLoader loader = new FXMLLoader(settingsUrl);
            Parent root = loader.load();
            SettingsController controller = loader.getController();
            Stage dialog = new Stage();
            dialog.setTitle("Settings");
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.WINDOW_MODAL);
            controller.setDialogStage(dialog);
            controller.setInitialSettings(gameSettings);
            Scene scene = new Scene(root, 520, 620);
            dialog.setScene(scene);
            dialog.showAndWait();
            controller.getResult().ifPresent(result -> {
                gameSettings = result;
                musicManager.setEnabled(gameSettings.isBgmEnabled());
                musicManager.setMasterVolume(gameSettings.getBgmVolume());
                if (gameSettings.isBgmEnabled()) {
                    musicManager.playMenuTheme();
                }
            });
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load settings dialog.", ex);
        }
    }

    @FXML
    private void onClearScores(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Clear Leaderboard");
        confirmation.setHeaderText("Remove all stored high scores?");
        confirmation.setContentText("This action cannot be undone.");
        if (primaryStage != null) {
            confirmation.initOwner(primaryStage);
        }
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            highScoreService.clear();
            refreshLeaderboard();
        }
    }

    @FXML
    private void onHelp(ActionEvent event) {
        ensurePrimaryStageBound();
        HelpDialogService.showHelp(primaryStage, helpContentProvider.getMarkdown());
    }

    private void ensurePrimaryStageBound() {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been injected into StartMenuController.");
        }
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig != null ? gameConfig : GameConfig.defaultConfig();
    }

    private void refreshLeaderboard() {
        if (leaderboardContainer == null || leaderboardEmptyLabel == null || clearScoresButton == null) {
            return;
        }
        List<HighScoreEntry> entries = highScoreService.fetchLeaderboard();
        leaderboardContainer.getChildren().clear();
        if (entries.isEmpty()) {
            leaderboardEmptyLabel.setManaged(true);
            leaderboardEmptyLabel.setVisible(true);
            leaderboardContainer.setManaged(false);
            leaderboardContainer.setVisible(false);
            clearScoresButton.setDisable(true);
            clearScoresButton.setVisible(false);
            clearScoresButton.setManaged(false);
            return;
        }
        int rank = 1;
        for (HighScoreEntry entry : entries) {
            Label row = new Label(formatEntry(rank++, entry));
            row.getStyleClass().add("leaderboard-entry");
            row.setAlignment(Pos.CENTER);
            row.setTextAlignment(TextAlignment.CENTER);
            row.setMaxWidth(Double.MAX_VALUE);
            leaderboardContainer.getChildren().add(row);
        }
        leaderboardContainer.setManaged(true);
        leaderboardContainer.setVisible(true);
        leaderboardEmptyLabel.setVisible(false);
        leaderboardEmptyLabel.setManaged(false);
        clearScoresButton.setDisable(false);
        clearScoresButton.setManaged(true);
        clearScoresButton.setVisible(true);
    }

    private String formatEntry(int rank, HighScoreEntry entry) {
        return String.format("#%d %s pts · %s · %s",
                rank,
                entry.getScore(),
                entry.getMode(),
                entry.formattedDuration());
    }

    private GameConfig.GameMode promptModeSelection() {
        ensurePrimaryStageBound();
        try {
            URL modeSelectUrl = ResourceManager.getUrl(ResourceManager.Asset.MODE_SELECT_FXML);
            FXMLLoader loader = new FXMLLoader(modeSelectUrl);
            Parent root = loader.load();
            ModeSelectController controller = loader.getController();
            Stage dialog = new Stage();
            dialog.setTitle("Select Game Mode");
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.WINDOW_MODAL);
            controller.setDialogStage(dialog);
            controller.setInitialMode(gameConfig.getMode());
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();
            Optional<GameConfig.GameMode> result = controller.getResult();
            return result.orElse(null);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load mode selection dialog.", ex);
        }
    }
}
