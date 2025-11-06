package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int HIDDEN_ROWS = 2;
    private static final double GAME_OVER_GUIDE_OFFSET_ROWS = 0.5;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private StackPane gameLayer;

    @FXML
    private Pane guidePane;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private GameState gameState = GameState.MENU;

    private Stage primaryStage;

    private Line gameOverGuideLine;
    private int cachedGameOverRow = Integer.MIN_VALUE;

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
                if (gameState == GameState.PLAYING
                        && isPause.getValue() == Boolean.FALSE
                        && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
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
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        applyBrickView(brick);


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        updateTimelinePlayback();
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
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
        brickPanel.setLayoutX(gameLayer.getLayoutX() + brick.getxPosition() * (brickPanel.getVgap() + BRICK_SIZE));
        brickPanel.setLayoutY(-42 + gameLayer.getLayoutY() + brick.getyPosition() * (brickPanel.getHgap() + BRICK_SIZE));
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                setRectangleData(brickData[i][j], rectangles[i][j]);
            }
        }
        updateGameOverGuide(brick.getGameOverRow());
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (gameState != GameState.PLAYING || isPause.getValue() == Boolean.TRUE) {
            gamePanel.requestFocus();
            return;
        }
        DownData downData = eventListener.onDownEvent(event);
        if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }
        refreshBrick(downData.getViewData());
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
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

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    private void startNewGameSession() {
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
}
