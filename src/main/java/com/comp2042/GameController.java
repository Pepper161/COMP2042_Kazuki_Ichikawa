package com.comp2042;

public class GameController implements InputEventListener {

    private static final int LOCK_DELAY_TICKS = 8;

    private final GuiController viewGuiController;
    private final Board board = new SimpleBoard(25, 10);
    private int lockDelayCounter = LOCK_DELAY_TICKS;
    private boolean lockPending = false;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return new DownData(null, board.getViewData());
        }
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            clearRow = handleLockDelay();
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
            resetLockDelay();
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        if (board.moveBrickLeft()) {
            resetLockDelay();
        }
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        if (board.moveBrickRight()) {
            resetLockDelay();
        }
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        if (board.rotateLeftBrick()) {
            resetLockDelay();
        }
        return board.getViewData();
    }


    @Override
    public ViewData createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        resetLockDelay();
        return board.getViewData();
    }

    private ClearRow handleLockDelay() {
        if (!lockPending) {
            lockPending = true;
            lockDelayCounter = LOCK_DELAY_TICKS;
            return null;
        }
        lockDelayCounter--;
        if (lockDelayCounter <= 0) {
            return lockCurrentPiece();
        }
        return null;
    }

    private ClearRow lockCurrentPiece() {
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        board.getScore().handleLineClear(clearRow.getLinesRemoved());
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }
        boolean isGameOver = board.createNewBrick();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        if (isGameOver) {
            viewGuiController.gameOver();
        }
        resetLockDelay();
        return clearRow;
    }

    private void resetLockDelay() {
        lockPending = false;
        lockDelayCounter = LOCK_DELAY_TICKS;
    }
}
