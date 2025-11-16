package com.comp2042.ui;

import com.comp2042.app.StartMenuController;
import com.comp2042.board.ViewData;
import com.comp2042.config.GameSettings;
import com.comp2042.game.GameState;
import com.comp2042.game.Score;
import com.comp2042.game.events.DownData;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.ui.input.AutoRepeatHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JavaFX controller for the gameplay scene. It renders the board layers, handles input,
 * and delegates gameplay actions to {@link com.comp2042.game.GameController}.
 */
public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int HIDDEN_ROWS = 2;
    private static final double GAME_OVER_GUIDE_OFFSET_ROWS = 0.5;
    private static final double BASE_GRAVITY_INTERVAL_MS = 400;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane ghostPanel;

    @FXML
    private HudPanel hudPanel;

    @FXML
    private NextQueuePanel nextQueuePanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private StackPane gameLayer;

    @FXML
    private Pane guidePane;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private GameState gameState = GameState.MENU;

    private Stage primaryStage;

    private Line gameOverGuideLine;
    private int cachedGameOverRow = Integer.MIN_VALUE;
    private GameSettings gameSettings = GameSettings.defaultSettings();
    private final Map<KeyCode, GameSettings.Action> actionByKey = new HashMap<>();
    private final Set<KeyCode> heldKeys = EnumSet.noneOf(KeyCode.class);
    private AutoRepeatHandler moveLeftRepeat;
    private AutoRepeatHandler moveRightRepeat;
    private AutoRepeatHandler softDropRepeat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        StackPane.setAlignment(gamePanel, Pos.TOP_LEFT);
        StackPane.setAlignment(guidePane, Pos.TOP_LEFT);
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode code = keyEvent.getCode();
                if (!heldKeys.add(code)) {
                    keyEvent.consume();
                    return;
                }
                handleKeyPressed(code);
                keyEvent.consume();
            }
        });
        gamePanel.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode code = keyEvent.getCode();
                heldKeys.remove(code);
                handleKeyReleased(code);
            }
        });
        gameOverPanel.setVisible(false);
        gameOverPanel.setManaged(false);
        gameOverPanel.setOnRestart(this::startNewGameSession);
        gameOverPanel.setOnMainMenu(this::returnToMainMenu);
        gameOverPanel.setOnExit(Platform::exit);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(BrickColorPalette.resolve(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        if (ghostPanel != null) {
            ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.getStyleClass().add("ghost-cell");
                    rectangle.setVisible(false);
                    ghostRectangles[i][j] = rectangle;
                    ghostPanel.add(rectangle, j, i);
                }
            }
        }
        applyBrickView(brick);


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(BASE_GRAVITY_INTERVAL_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        updateTimelinePlayback();
        rebuildKeyBindings();
    }


    private void refreshBrick(ViewData brick) {
        if (brick == null) {
            return;
        }
        if (gameState == GameState.PLAYING && isPause.getValue() == Boolean.FALSE) {
            applyBrickView(brick);
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void applyBrickView(ViewData brick) {
        if (brick == null || rectangles == null || brickPanel == null || gameLayer == null) {
            return;
        }
        double originX = gameLayer.getLayoutX();
        double originY = -42 + gameLayer.getLayoutY();
        double cellWidth = brickPanel.getVgap() + BRICK_SIZE;
        double cellHeight = brickPanel.getHgap() + BRICK_SIZE;
        double activeX = originX + brick.getxPosition() * cellWidth;
        double activeY = originY + brick.getyPosition() * cellHeight;
        brickPanel.setLayoutX(activeX);
        brickPanel.setLayoutY(activeY);
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                setRectangleData(brickData[i][j], rectangles[i][j]);
            }
        }
        if (ghostPanel != null && ghostRectangles != null) {
            ghostPanel.setLayoutX(activeX);
            double ghostY = originY + brick.getGhostYPosition() * cellHeight;
            ghostPanel.setLayoutY(ghostY);
            for (int i = 0; i < brickData.length; i++) {
                for (int j = 0; j < brickData[i].length; j++) {
                    setGhostRectangleData(brickData[i][j], ghostRectangles[i][j]);
                }
            }
        }
        if (nextQueuePanel != null) {
            nextQueuePanel.setQueue(brick.getNextBricksData());
        }
        updateGameOverGuide(brick.getGameOverRow());
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(BrickColorPalette.resolve(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void setGhostRectangleData(int color, Rectangle rectangle) {
        if (rectangle == null) {
            return;
        }
        if (color == 0) {
            rectangle.setVisible(false);
            rectangle.setFill(Color.TRANSPARENT);
        } else {
            rectangle.setVisible(true);
            rectangle.setFill(BrickColorPalette.resolveGhost(color));
        }
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void handleDropResult(DownData downData) {
        if (downData == null) {
            return;
        }
        if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }
        refreshBrick(downData.getViewData());
        gamePanel.requestFocus();
    }

    private void moveDown(MoveEvent event) {
        if (gameState != GameState.PLAYING || isPause.getValue() == Boolean.TRUE) {
            gamePanel.requestFocus();
            return;
        }
        DownData downData = eventListener.onDownEvent(event);
        handleDropResult(downData);
    }

    private void hardDrop(MoveEvent event) {
        if (gameState != GameState.PLAYING || isPause.getValue() == Boolean.TRUE) {
            gamePanel.requestFocus();
            return;
        }
        DownData downData = eventListener.onHardDrop(event);
        handleDropResult(downData);
    }

    private void performMoveLeft() {
        if (eventListener == null) {
            return;
        }
        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
    }

    private void performMoveRight() {
        if (eventListener == null) {
            return;
        }
        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
    }

    private void performRotateClockwise() {
        if (eventListener == null) {
            return;
        }
        refreshBrick(eventListener.onRotateClockwise(new MoveEvent(EventType.ROTATE_CLOCKWISE, EventSource.USER)));
    }

    private void performRotateCounterClockwise() {
        if (eventListener == null) {
            return;
        }
        refreshBrick(eventListener.onRotateCounterClockwise(new MoveEvent(EventType.ROTATE_COUNTERCLOCKWISE, EventSource.USER)));
    }

    private void performSoftDrop() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
    }

    private void performHardDrop() {
        hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(Score score) {
        if (hudPanel != null && score != null) {
            hudPanel.bindScore(score);
        }
    }

    public void setGameSettings(GameSettings settings) {
        this.gameSettings = settings != null ? settings : GameSettings.defaultSettings();
        heldKeys.clear();
        rebuildKeyBindings();
    }

    private void rebuildKeyBindings() {
        actionByKey.clear();
        if (gameSettings == null) {
            gameSettings = GameSettings.defaultSettings();
        }
        for (Map.Entry<GameSettings.Action, KeyCode> entry : gameSettings.getKeyBindings().entrySet()) {
            if (entry.getValue() != null) {
                actionByKey.put(entry.getValue(), entry.getKey());
            }
        }
        Duration das = Duration.millis(gameSettings.getDasDelayMs());
        Duration arr = Duration.millis(Math.max(1, gameSettings.getArrIntervalMs()));
        moveLeftRepeat = new AutoRepeatHandler(this::performMoveLeft, das, arr);
        moveRightRepeat = new AutoRepeatHandler(this::performMoveRight, das, arr);
        double softDropInterval = Math.max(10d, BASE_GRAVITY_INTERVAL_MS / gameSettings.getSoftDropMultiplier());
        softDropRepeat = new AutoRepeatHandler(this::performSoftDrop, Duration.ZERO, Duration.millis(softDropInterval));
    }

    private void handleKeyPressed(KeyCode code) {
        GameSettings.Action action = actionByKey.get(code);
        if (action == null) {
            return;
        }
        if (action == GameSettings.Action.NEW_GAME) {
            newGame(null);
            return;
        }
        if (!isGameplayInputEnabled()) {
            return;
        }
        switch (action) {
            case MOVE_LEFT -> {
                if (moveLeftRepeat != null) {
                    moveLeftRepeat.start();
                }
            }
            case MOVE_RIGHT -> {
                if (moveRightRepeat != null) {
                    moveRightRepeat.start();
                }
            }
            case SOFT_DROP -> {
                if (softDropRepeat != null) {
                    softDropRepeat.start();
                } else {
                    performSoftDrop();
                }
            }
            case HARD_DROP -> performHardDrop();
            case ROTATE_CW -> performRotateClockwise();
            case ROTATE_CCW -> performRotateCounterClockwise();
            default -> {
            }
        }
    }

    private void handleKeyReleased(KeyCode code) {
        GameSettings.Action action = actionByKey.get(code);
        if (action == null) {
            return;
        }
        switch (action) {
            case MOVE_LEFT -> stopRepeat(moveLeftRepeat);
            case MOVE_RIGHT -> stopRepeat(moveRightRepeat);
            case SOFT_DROP -> stopRepeat(softDropRepeat);
            default -> {
            }
        }
    }

    private boolean isGameplayInputEnabled() {
        return gameState == GameState.PLAYING
                && Boolean.FALSE.equals(isPause.getValue())
                && Boolean.FALSE.equals(isGameOver.getValue());
    }

    private void stopRepeat(AutoRepeatHandler handler) {
        if (handler != null) {
            handler.stop();
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void gameOver() {
        setGameState(GameState.GAME_OVER);
    }

    public void newGame(ActionEvent actionEvent) {
        startNewGameSession();
    }

    public void updateSeedInfo(long seed, boolean deterministic) {
        if (gameOverPanel != null) {
            gameOverPanel.setSeedInfo(seed, deterministic);
        }
    }

    private void startNewGameSession() {
        stopAllRepeats();
        if (timeLine != null) {
            timeLine.stop();
        }
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
            gameOverPanel.setManaged(false);
        }
        if (eventListener == null) {
            return;
        }
        ViewData freshState = eventListener.createNewGame();
        applyBrickView(freshState);
        setGameState(GameState.PLAYING);
    }

    private void returnToMainMenu() {
        if (timeLine != null) {
            timeLine.stop();
        }
        setGameState(GameState.MENU);
        stopAllRepeats();
        if (gamePanel != null) {
            gamePanel.setFocusTraversable(false);
        }
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been set on GuiController.");
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("StartMenu.fxml"));
            Parent menuRoot = loader.load();
            StartMenuController menuController = loader.getController();
            menuController.setPrimaryStage(primaryStage);
            Scene currentScene = primaryStage.getScene();
            double width = currentScene != null ? currentScene.getWidth() : StartMenuController.WINDOW_WIDTH;
            double height = currentScene != null ? currentScene.getHeight() : StartMenuController.WINDOW_HEIGHT;
            Scene menuScene = new Scene(menuRoot, width, height);
            primaryStage.setScene(menuScene);
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load StartMenu.fxml", e);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState newGameState) {
        if (newGameState == null) {
            throw new IllegalArgumentException("GameState cannot be null");
        }
        if (gameState == newGameState) {
            updateTimelinePlayback();
            return;
        }
        gameState = newGameState;
        switch (gameState) {
            case MENU:
                isPause.setValue(Boolean.TRUE);
                isGameOver.setValue(Boolean.FALSE);
                if (gameOverPanel != null) {
                    gameOverPanel.setVisible(false);
                    gameOverPanel.setManaged(false);
                }
                stopAllRepeats();
                break;
            case PLAYING:
                isPause.setValue(Boolean.FALSE);
                isGameOver.setValue(Boolean.FALSE);
                if (gameOverPanel != null) {
                    gameOverPanel.setVisible(false);
                    gameOverPanel.setManaged(false);
                }
                break;
            case GAME_OVER:
                isPause.setValue(Boolean.TRUE);
                isGameOver.setValue(Boolean.TRUE);
                if (gameOverPanel != null) {
                    gameOverPanel.setVisible(true);
                    gameOverPanel.setManaged(true);
                }
                stopAllRepeats();
                break;
            default:
                throw new IllegalStateException("Unhandled game state: " + gameState);
        }
        updateTimelinePlayback();
        if (gameState == GameState.PLAYING && gamePanel != null) {
            gamePanel.requestFocus();
        }
    }

    private void updateTimelinePlayback() {
        if (timeLine == null) {
            return;
        }
        if (gameState == GameState.PLAYING) {
            timeLine.play();
        } else {
            timeLine.stop();
        }
    }

    private void updateGameOverGuide(int gameOverRow) {
        if (guidePane == null || displayMatrix == null) {
            return;
        }
        if (cachedGameOverRow == gameOverRow && gameOverGuideLine != null) {
            return;
        }
        int columns = displayMatrix[0].length;
        int visibleRows = displayMatrix.length - HIDDEN_ROWS;
        double rowHeight = BRICK_SIZE + gamePanel.getVgap();
        double boardWidth = columns * BRICK_SIZE + Math.max(0, columns - 1) * gamePanel.getHgap();
        double boardHeight = visibleRows * BRICK_SIZE + Math.max(0, visibleRows - 1) * gamePanel.getVgap();
        guidePane.setPrefWidth(boardWidth);
        guidePane.setPrefHeight(boardHeight);
        double y = (gameOverRow - HIDDEN_ROWS + GAME_OVER_GUIDE_OFFSET_ROWS) * rowHeight;
        if (y < 0) {
            y = 0;
        }
        if (gameOverGuideLine == null) {
            gameOverGuideLine = new Line(0, y, boardWidth, y);
            gameOverGuideLine.setStroke(Color.rgb(220, 20, 60));
            gameOverGuideLine.setOpacity(0.5);
            gameOverGuideLine.setStrokeWidth(2);
            guidePane.getChildren().add(gameOverGuideLine);
        } else {
            gameOverGuideLine.setStartX(0);
            gameOverGuideLine.setEndX(boardWidth);
            gameOverGuideLine.setStartY(y);
            gameOverGuideLine.setEndY(y);
        }
        cachedGameOverRow = gameOverRow;
    }

    private void stopAllRepeats() {
        stopRepeat(moveLeftRepeat);
        stopRepeat(moveRightRepeat);
        stopRepeat(softDropRepeat);
        heldKeys.clear();
    }
}
