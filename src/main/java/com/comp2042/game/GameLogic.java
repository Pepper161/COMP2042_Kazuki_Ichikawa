package com.comp2042.game;

import com.comp2042.board.Board;
import com.comp2042.board.ClearRow;
import com.comp2042.board.MatrixOperations;
import com.comp2042.board.ViewData;
import com.comp2042.game.events.DownData;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.logic.bricks.TetrominoType;

/**
 * Encapsulates gameplay mechanics (movement, lock delay, scoring) so the controller
 * can remain focused on UI orchestration.
 */
public class GameLogic {

    private static final int LOCK_DELAY_TICKS = 2;
    private static final int LOCK_RESET_CAP = 5;
    private static final int HARD_DROP_POINTS_PER_CELL = 2;

    private enum LastAction {
        NONE,
        MOVE,
        ROTATE
    }

    private final Board board;
    private int lockDelayCounter = LOCK_DELAY_TICKS;
    private boolean lockPending = false;
    private boolean boardRefreshRequested = false;
    private boolean gameOverTriggered = false;
    private int lockResetsRemaining = LOCK_RESET_CAP;
    private LastAction lastAction = LastAction.NONE;

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
            lastAction = LastAction.MOVE;
        }
        return new DownData(clearRow, board.getViewData());
    }

    public ViewData moveLeft() {
        if (board.moveBrickLeft()) {
            refreshLockDelayIfGrounded();
            lastAction = LastAction.MOVE;
        }
        return board.getViewData();
    }

    public ViewData moveRight() {
        if (board.moveBrickRight()) {
            refreshLockDelayIfGrounded();
            lastAction = LastAction.MOVE;
        }
        return board.getViewData();
    }

    public ViewData rotateClockwise() {
        if (board.rotateClockwise()) {
            refreshLockDelayIfGrounded();
            lastAction = LastAction.ROTATE;
        }
        return board.getViewData();
    }

    public ViewData rotateCounterClockwise() {
        if (board.rotateCounterClockwise()) {
            refreshLockDelayIfGrounded();
            lastAction = LastAction.ROTATE;
        }
        return board.getViewData();
    }

    public DownData hardDrop(MoveEvent event) {
        int dropDistance = 0;
        while (board.moveBrickDown()) {
            dropDistance++;
        }
        if (dropDistance > 0) {
            board.getScore().add(dropDistance * HARD_DROP_POINTS_PER_CELL);
        }
        lastAction = LastAction.MOVE;
        ClearRow clearRow = lockCurrentPiece();
        return new DownData(clearRow, board.getViewData());
    }

    public ViewData newGame() {
        board.newGame();
        resetLockDelayState();
        boardRefreshRequested = true;
        lastAction = LastAction.NONE;
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
        int[][] boardBeforeLock = MatrixOperations.copy(board.getBoardMatrix());
        ViewData activeView = board.getViewData();
        TetrominoType activeType = board.getActiveTetrominoType();

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        LineClearStats stats = classifyLineClear(clearRow.getLinesRemoved(), activeType, activeView, boardBeforeLock);
        board.getScore().handleLineClear(stats);
        boolean newBrickConflict = board.createNewBrick();
        boardRefreshRequested = true;
        if (newBrickConflict) {
            gameOverTriggered = true;
        }
        resetLockDelayState();
        lastAction = LastAction.NONE;
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

    private LineClearStats classifyLineClear(int linesRemoved,
                                             TetrominoType activeType,
                                             ViewData activeView,
                                             int[][] boardBeforeLock) {
        if (linesRemoved <= 0 || activeView == null) {
            return new LineClearStats(LineClearType.NONE, 0);
        }
        boolean isTSpin = isTSpinClear(activeType, activeView, boardBeforeLock);
        LineClearType clearType = determineClearType(linesRemoved, isTSpin);
        return new LineClearStats(clearType, linesRemoved);
    }

    private boolean isTSpinClear(TetrominoType activeType, ViewData activeView, int[][] boardBeforeLock) {
        if (activeType != TetrominoType.T || lastAction != LastAction.ROTATE) {
            return false;
        }
        int anchorX = activeView.getxPosition() + 1;
        int anchorY = activeView.getyPosition() + 1;
        if (anchorY < 0) {
            return false;
        }
        int occupiedCorners = countOccupiedCorners(boardBeforeLock, anchorX, anchorY);
        return occupiedCorners >= 3;
    }

    private int countOccupiedCorners(int[][] boardMatrix, int anchorX, int anchorY) {
        int[][] offsets = {
                {-1, -1},
                {1, -1},
                {-1, 1},
                {1, 1}
        };
        int occupied = 0;
        for (int[] offset : offsets) {
            int x = anchorX + offset[0];
            int y = anchorY + offset[1];
            if (y < 0) {
                occupied++;
                continue;
            }
            if (x < 0 || y >= boardMatrix.length || x >= boardMatrix[y].length) {
                occupied++;
                continue;
            }
            if (boardMatrix[y][x] != 0) {
                occupied++;
            }
        }
        return occupied;
    }

    private LineClearType determineClearType(int linesRemoved, boolean isTSpin) {
        if (isTSpin) {
            switch (linesRemoved) {
                case 1:
                    return LineClearType.T_SPIN_SINGLE;
                case 2:
                    return LineClearType.T_SPIN_DOUBLE;
                case 3:
                    return LineClearType.T_SPIN_TRIPLE;
                default:
                    return LineClearType.NONE;
            }
        }
        switch (linesRemoved) {
            case 1:
                return LineClearType.SINGLE;
            case 2:
                return LineClearType.DOUBLE;
            case 3:
                return LineClearType.TRIPLE;
            case 4:
                return LineClearType.TETRIS;
            default:
                return LineClearType.NONE;
        }
    }
}
