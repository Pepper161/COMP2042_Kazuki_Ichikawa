package com.comp2042.app;

import com.comp2042.game.GameConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Modal controller that lets the player pick a {@link GameConfig.GameMode}.
 * Mirrors the Gemini-provided card layout so the JavaFX scene matches the design.
 */
public class ModeSelectController {

    @FXML
    private FlowPane modeGrid;

    @FXML
    private Label detailTitle;

    @FXML
    private Label detailDescription;

    @FXML
    private Label detailMeta;

    @FXML
    private Button startModeButton;

    private final Map<GameConfig.GameMode, VBox> modeCards = new EnumMap<>(GameConfig.GameMode.class);
    private Stage dialogStage;
    private GameConfig.GameMode selectedMode;
    private boolean confirmed;

    @FXML
    public void initialize() {
        createModeCards();
        updateDetail(null);
        startModeButton.setDisable(true);
    }

    private void createModeCards() {
        if (modeGrid == null) {
            return;
        }
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            VBox card = buildCard(mode);
            modeCards.put(mode, card);
            modeGrid.getChildren().add(card);
        }
    }

    private VBox buildCard(GameConfig.GameMode mode) {
        ModeInfo info = ModeInfo.forMode(mode);

        Label title = new Label(info.title());
        title.getStyleClass().add("mode-card-title");

        Label summary = new Label(info.description());
        summary.setWrapText(true);
        summary.getStyleClass().add("mode-card-description");

        HBox badges = new HBox(8);
        badges.getStyleClass().add("mode-badges");
        for (String badge : info.badges()) {
            Label badgeLabel = new Label(badge);
            badgeLabel.getStyleClass().add("mode-badge");
            badges.getChildren().add(badgeLabel);
        }

        VBox card = new VBox(10, title, summary, badges);
        card.getStyleClass().add("mode-card");
        card.setFocusTraversable(true);
        card.setOnMouseClicked(event -> selectMode(mode));
        card.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.ENTER) {
                selectMode(mode);
            }
        });
        return card;
    }

    private void selectMode(GameConfig.GameMode mode) {
        selectedMode = mode;
        modeCards.forEach((candidate, card) -> {
            if (candidate == mode) {
                if (!card.getStyleClass().contains("selected")) {
                    card.getStyleClass().add("selected");
                }
            } else {
                card.getStyleClass().remove("selected");
            }
        });
        updateDetail(mode);
        startModeButton.setDisable(false);
    }

    private void updateDetail(GameConfig.GameMode mode) {
        if (mode == null) {
            detailTitle.setText("Select a mode to view details");
            detailDescription.setText("Each ruleset tunes speed ramps and objectives.");
            detailMeta.setText("");
            return;
        }
        ModeInfo info = ModeInfo.forMode(mode);
        detailTitle.setText(info.title());
        detailDescription.setText(info.longDescription());
        detailMeta.setText(info.meta());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setInitialMode(GameConfig.GameMode mode) {
        if (mode == null) {
            return;
        }
        VBox card = modeCards.get(mode);
        if (card != null) {
            selectMode(mode);
        }
    }

    @FXML
    private void onCancel() {
        confirmed = false;
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void onConfirm() {
        confirmed = true;
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    public Optional<GameConfig.GameMode> getResult() {
        if (confirmed) {
            return Optional.ofNullable(selectedMode);
        }
        return Optional.empty();
    }

    private record ModeInfo(String title, String description, String longDescription, String meta, String[] badges) {

        static ModeInfo forMode(GameConfig.GameMode mode) {
            return switch (mode) {
                case ENDLESS -> new ModeInfo(
                        "Endless Marathon",
                        "Survive as long as you can; speed ramps every 10 lines.",
                        "Classic endless rules. Variable gravity keeps tension rising the longer you last.",
                        "Objective: Clear as many lines as possible.",
                        new String[]{"Relaxed", "Scaling Speed"}
                );
                case TIMED -> new ModeInfo(
                        "Timed Sprint",
                        "Score as much as possible before the 180s timer expires.",
                        "Combos and T-Spins are king in sprint. Stay aggressive and keep the board low.",
                        "Objective: Highest score within 180 seconds.",
                        new String[]{"180s", "High Pressure"}
                );
                case FIXED_LINES -> new ModeInfo(
                        "Fixed Lines",
                        "Clear exactly 40 lines with the best efficiency.",
                        "Speedrunner favourite. Trim misdrops quickly to keep splits green.",
                        "Objective: Reach 40 lines fast.",
                        new String[]{"40 Lines", "Speedrun"}
                );
            };
        }
    }
}
