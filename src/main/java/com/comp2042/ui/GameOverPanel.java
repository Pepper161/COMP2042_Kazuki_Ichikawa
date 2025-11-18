package com.comp2042.ui;

import com.comp2042.game.stats.HighScoreEntry;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Overlay shown when the game reaches a GAME OVER state.
 */
public class GameOverPanel extends BorderPane {

    private Runnable onRestart = () -> {};
    private Runnable onExit = () -> {};
    private Runnable onMainMenu = () -> {};
    private final Label seedLabel = new Label();
    private final Label leaderboardTitle = new Label("Top Scores");
    private final VBox leaderboardContainer = new VBox(4);
    private final Label emptyLeaderboardLabel = new Label("No runs recorded yet.");

    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        seedLabel.getStyleClass().add("seedLabel");
        seedLabel.setVisible(false);

        leaderboardTitle.getStyleClass().add("menu-subtitle");
        emptyLeaderboardLabel.getStyleClass().add("leaderboard-placeholder");
        leaderboardContainer.setAlignment(Pos.CENTER_LEFT);

        Button restartButton = new Button("Restart");
        restartButton.getStyleClass().add("ipad-dark-grey");
        restartButton.setOnAction(event -> onRestart.run());

        Button menuButton = new Button("Main Menu");
        menuButton.getStyleClass().add("ipad-dark-grey");
        menuButton.setOnAction(event -> onMainMenu.run());

        Button exitButton = new Button("Exit");
        exitButton.getStyleClass().add("ipad-dark-grey");
        exitButton.setOnAction(event -> onExit.run());

        VBox leaderboardBox = new VBox(6, leaderboardTitle, emptyLeaderboardLabel, leaderboardContainer);
        leaderboardBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(18, gameOverLabel, seedLabel, leaderboardBox, restartButton, menuButton, exitButton);
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

    public void setSeedInfo(long seed, boolean deterministic) {
        seedLabel.setText("Seed: " + seed + (deterministic ? " (fixed)" : ""));
        seedLabel.setVisible(true);
    }

    public void setLeaderboard(List<HighScoreEntry> entries, HighScoreEntry highlight) {
        leaderboardContainer.getChildren().clear();
        boolean hasEntries = entries != null && !entries.isEmpty();
        leaderboardContainer.setManaged(hasEntries);
        leaderboardContainer.setVisible(hasEntries);
        emptyLeaderboardLabel.setManaged(!hasEntries);
        emptyLeaderboardLabel.setVisible(!hasEntries);
        if (!hasEntries) {
            return;
        }
        int rank = 1;
        for (HighScoreEntry entry : entries) {
            Label label = new Label(formatEntry(rank++, entry));
            label.getStyleClass().add("leaderboard-entry");
            if (highlight != null && highlight.equals(entry)) {
                label.getStyleClass().add("leaderboard-highlight");
            }
            leaderboardContainer.getChildren().add(label);
        }
    }

    private String formatEntry(int rank, HighScoreEntry entry) {
        return String.format("#%d %s pts · %s · %s",
                rank,
                entry.getScore(),
                entry.getMode(),
                entry.formattedDuration());
    }
}
