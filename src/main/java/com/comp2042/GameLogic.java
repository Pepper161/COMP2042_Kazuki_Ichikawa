package com.comp2042;

/**
 * Encapsulates gameplay mechanics (movement, lock delay, scoring) so the controller
 * can remain focused on UI orchestration.
 */
public class GameLogic {

    private static final int LOCK_DELAY_TICKS = 8;

    private final Board board;
    private int lockDelayCounter = LOCK_DELAY_TICKS;
    private boolean lockPending = false;
    private boolean boardRefreshRequested = false;
    private boolean gameOverTriggered = false;

    public GameLogic(Board board) {
        this.board = board;
    }

    public DownData moveDown(MoveEvent event) {
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

    public ViewData moveLeft() {
        if (board.moveBrickLeft()) {
            resetLockDelay();
        }
        return board.getViewData();
    }

    public ViewData moveRight() {
        if (board.moveBrickRight()) {
            resetLockDelay();
        }
        return board.getViewData();
    }

    public ViewData rotate() {
        if (board.rotateLeftBrick()) {
            resetLockDelay();
        }
        return board.getViewData();
    }

    public ViewData newGame() {
        board.newGame();
        resetLockDelay();
        boardRefreshRequested = true;
        return board.getViewData();
    }

    public boolean consumeBoardRefreshFlag() {
        boolean result = boardRefreshRequested;
        boardRefreshRequested = false;
        return result;
    }

    public boolean consumeGameOverFlag() {
        boolean result = gameOverTriggered;
        gameOverTriggered = false;
        return result;
    }

    public Score getScore() {
        return board.getScore();
    }

    public Board getBoard() {
        return board;
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
        boolean newBrickConflict = board.createNewBrick();
        boardRefreshRequested = true;
        if (newBrickConflict) {
            gameOverTriggered = true;
        }
        resetLockDelay();
        return clearRow;
    }

    private void resetLockDelay() {
        lockPending = false;
        lockDelayCounter = LOCK_DELAY_TICKS;
    }
}
