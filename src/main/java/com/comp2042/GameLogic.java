package com.comp2042;

/**
 * Encapsulates gameplay mechanics (movement, lock delay, scoring) so the controller
 * can remain focused on UI orchestration.
 */
public class GameLogic {

    private static final int LOCK_DELAY_TICKS = 8;
    private static final int LOCK_RESET_CAP = 15;

    private final Board board;
    private int lockDelayCounter = LOCK_DELAY_TICKS;
    private boolean lockPending = false;
    private boolean boardRefreshRequested = false;
    private boolean gameOverTriggered = false;
    private int lockResetsRemaining = LOCK_RESET_CAP;

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
            resetLockDelayState();
        }
        return new DownData(clearRow, board.getViewData());
    }

    public ViewData moveLeft() {
        if (board.moveBrickLeft()) {
            refreshLockDelayIfGrounded();
        }
        return board.getViewData();
    }

    public ViewData moveRight() {
        if (board.moveBrickRight()) {
            refreshLockDelayIfGrounded();
        }
        return board.getViewData();
    }

    public ViewData rotateClockwise() {
        if (board.rotateClockwise()) {
            refreshLockDelayIfGrounded();
        }
        return board.getViewData();
    }

    public ViewData rotateCounterClockwise() {
        if (board.rotateCounterClockwise()) {
            refreshLockDelayIfGrounded();
        }
        return board.getViewData();
    }

    public ViewData newGame() {
        board.newGame();
        resetLockDelayState();
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
            lockResetsRemaining = LOCK_RESET_CAP;
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
        resetLockDelayState();
        return clearRow;
    }

    private void resetLockDelayState() {
        lockPending = false;
        lockDelayCounter = LOCK_DELAY_TICKS;
        lockResetsRemaining = LOCK_RESET_CAP;
    }

    private void refreshLockDelayIfGrounded() {
        if (!lockPending || lockResetsRemaining <= 0) {
            return;
        }
        lockDelayCounter = LOCK_DELAY_TICKS;
        lockResetsRemaining--;
    }
}
