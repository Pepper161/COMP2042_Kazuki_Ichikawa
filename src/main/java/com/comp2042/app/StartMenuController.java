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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.stage.Modality;

/**
 * Controller for the start menu. Handles navigation into the gameplay scene.
 */
public class StartMenuController {

    public static final double MENU_WINDOW_WIDTH = 1024;
    public static final double MENU_WINDOW_HEIGHT = 720;
    public static final double GAME_WINDOW_WIDTH = 1120;
    public static final double GAME_WINDOW_HEIGHT = 760;

    private Stage primaryStage;
    private GameConfig gameConfig = GameConfig.defaultConfig();
    private final GameSettingsStore settingsStore = new GameSettingsStore();
    private GameSettings gameSettings = settingsStore.load();
    private final BackgroundMusicManager musicManager = BackgroundMusicManager.getInstance();
    private final HighScoreService highScoreService = new HighScoreService();
    private final HelpContentProvider helpContentProvider = HelpContentProvider.getInstance();

    @FXML
    private VBox leaderboardSections;

    @FXML
    private Button clearScoresButton;

    @FXML
    private Label titleLabel;

    private final Map<GameConfig.GameMode, VBox> leaderboardLists = new EnumMap<>(GameConfig.GameMode.class);
    private final Map<GameConfig.GameMode, Label> leaderboardPlaceholders = new EnumMap<>(GameConfig.GameMode.class);

    @FXML
    public void initialize() {
        buildLeaderboardSections();
        refreshLeaderboard();
        startTitleAnimation();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(MENU_WINDOW_WIDTH);
        primaryStage.setMinHeight(MENU_WINDOW_HEIGHT);
        primaryStage.setMaxWidth(MENU_WINDOW_WIDTH);
        primaryStage.setMaxHeight(MENU_WINDOW_HEIGHT);
        musicManager.setEnabled(gameSettings.isBgmEnabled());
        musicManager.setMasterVolume(gameSettings.getBgmVolume());
        musicManager.playMenuTheme();
    }

    /**
     * Start the color-shifting animation for the title label.
     * Cycles between cyan (#00f3ff) and magenta (#ff0055) continuously.
     */
    private void startTitleAnimation() {
        if (titleLabel == null) {
            return; // Guard against null reference
        }

        // Define colors: Cyan and Magenta
        Color cyan = Color.web("#00f3ff");
        Color magenta = Color.web("#ff0055");

        // Create a timeline that cycles colors
        Timeline colorTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(titleLabel.textFillProperty(), cyan)),
            new KeyFrame(Duration.seconds(3), new KeyValue(titleLabel.textFillProperty(), magenta)),
            new KeyFrame(Duration.seconds(6), new KeyValue(titleLabel.textFillProperty(), cyan))
        );

        // Loop infinitely
        colorTimeline.setCycleCount(Timeline.INDEFINITE);
        colorTimeline.play();
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
        List<HighScoreEntry> entries = highScoreService.fetchLeaderboard();
        Map<GameConfig.GameMode, List<HighScoreEntry>> grouped = new EnumMap<>(GameConfig.GameMode.class);
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            grouped.put(mode, new ArrayList<>());
        }
        for (HighScoreEntry entry : entries) {
            grouped.get(resolveMode(entry)).add(entry);
        }
        boolean hasAny = false;
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            List<HighScoreEntry> modeEntries = grouped.getOrDefault(mode, List.of());
            VBox listBox = leaderboardLists.get(mode);
            Label placeholder = leaderboardPlaceholders.get(mode);
            listBox.getChildren().clear();
            if (modeEntries.isEmpty()) {
                placeholder.setManaged(true);
                placeholder.setVisible(true);
                listBox.setManaged(false);
                listBox.setVisible(false);
            } else {
                placeholder.setManaged(false);
                placeholder.setVisible(false);
                listBox.setManaged(true);
                listBox.setVisible(true);
                int rank = 1;
                for (HighScoreEntry entry : modeEntries) {
                    Label row = new Label(formatEntry(rank++, entry));
                    row.getStyleClass().add("leaderboard-entry");
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setMaxWidth(Double.MAX_VALUE);
                    listBox.getChildren().add(row);
                }
                hasAny = true;
            }
        }
        if (clearScoresButton != null) {
            clearScoresButton.setDisable(!hasAny);
            clearScoresButton.setManaged(hasAny);
            clearScoresButton.setVisible(hasAny);
        }
    }

    private void buildLeaderboardSections() {
        if (leaderboardSections == null) {
            return;
        }
        leaderboardSections.getChildren().clear();
        leaderboardLists.clear();
        leaderboardPlaceholders.clear();
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            VBox section = new VBox(8);
            section.getStyleClass().add("leaderboard-section");
            Label title = new Label(mode.toString());
            title.getStyleClass().add("leaderboard-section-title");

            VBox list = new VBox(4);
            list.getStyleClass().add("lb-list");

            Label placeholder = new Label("Play this mode to record a high score.");
            placeholder.getStyleClass().add("leaderboard-placeholder");
            placeholder.setWrapText(true);

            section.getChildren().addAll(title, list, placeholder);
            leaderboardSections.getChildren().add(section);
            leaderboardLists.put(mode, list);
            leaderboardPlaceholders.put(mode, placeholder);
        }
    }

    private GameConfig.GameMode resolveMode(HighScoreEntry entry) {
        String modeLabel = entry != null ? entry.getMode() : "";
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            if (mode.toString().equalsIgnoreCase(modeLabel)) {
                return mode;
            }
        }
        return GameConfig.GameMode.ENDLESS;
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
