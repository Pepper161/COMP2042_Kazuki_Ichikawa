package com.comp2042.game;

import com.comp2042.board.Board;
import com.comp2042.board.SimpleBoard;
import com.comp2042.board.ViewData;
import com.comp2042.config.GameSettings;
import com.comp2042.game.events.DownData;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.logic.bricks.PieceGenerator;
import com.comp2042.ui.GuiController;

/**
 * Wires {@link GuiController} input callbacks to {@link GameLogic} and keeps the board/score in sync.
 */
public class GameController implements InputEventListener {

    private final GuiController viewGuiController;
    private final GameConfig config;
    private final PieceGenerator generator;
    private final Board board;
    private final GameLogic logic;

    public GameController(GuiController c, GameConfig config, GameSettings settings) {
        viewGuiController = c;
        this.config = config != null ? config : GameConfig.defaultConfig();
        GameSettings safeSettings = settings != null ? settings : GameSettings.defaultSettings();
        generator = this.config.seedOverride().isPresent()
                ? new PieceGenerator(this.config.seedOverride().getAsLong())
                : new PieceGenerator();
        board = new SimpleBoard(25, 10, generator);
        logic = new GameLogic(board);
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.setGameSettings(safeSettings);
        viewGuiController.setGameMode(this.config.getMode());
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(logic.getScore());
        viewGuiController.prepareModeSession();
        publishSeedSummary();
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return new DownData(null, board.getViewData());
        }
        DownData downData = logic.moveDown(event);
        syncBoardState();
        return downData;
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.moveLeft();
        syncBoardState();
        return data;
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.moveRight();
        syncBoardState();
        return data;
    }

    @Override
    public ViewData onRotateClockwise(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.rotateClockwise();
        syncBoardState();
        return data;
    }

    @Override
    public ViewData onRotateCounterClockwise(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.rotateCounterClockwise();
        syncBoardState();
        return data;
    }


    @Override
    public DownData onHardDrop(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return new DownData(null, board.getViewData());
        }
        DownData data = logic.hardDrop(event);
        syncBoardState();
        return data;
    }

    @Override
    public ViewData createNewGame() {
        ViewData viewData = logic.newGame();
        publishSeedSummary();
        syncBoardState();
        return viewData;
    }

    private void syncBoardState() {
        if (logic.consumeBoardRefreshFlag()) {
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }
        if (logic.consumeGameOverFlag()) {
            logSeedForReplay();
            viewGuiController.gameOver();
        }
    }

    private void publishSeedSummary() {
        long seed = generator.getCurrentSeed();
        boolean deterministic = config.hasSeedOverride();
        viewGuiController.updateSeedInfo(seed, deterministic);
        System.out.println("[Game] Session seed: " + seed + (deterministic ? " (fixed)" : ""));
    }

    private void logSeedForReplay() {
        long seed = generator.getCurrentSeed();
        System.out.println("[Game] Game over. Replay this run with --seed=" + seed);
    }
}
