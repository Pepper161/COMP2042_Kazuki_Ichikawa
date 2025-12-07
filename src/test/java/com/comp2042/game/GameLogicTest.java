package com.comp2042.game;

import com.comp2042.board.Board;
import com.comp2042.board.ClearRow;
import com.comp2042.board.MatrixOperations;
import com.comp2042.board.ViewData;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.logic.bricks.TetrominoType;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameLogicTest {

    @Test
    void hardDropAwardsDropPointsAndLineClearScore() {
        StubBoard board = new StubBoard();
        board.downMovesRemaining = 3;
        board.boardMatrix = new int[][]{
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1}
        };
        board.nextClear = new ClearRow(2, board.boardMatrix, 0);
        board.viewData = new ViewData(new int[][]{{1}}, 0, 0, 0, Collections.emptyList(), 0);
        board.tetrominoType = TetrominoType.I;

        GameLogic logic = new GameLogic(board);
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);

        var downData = logic.hardDrop(event);

        assertNotNull(downData.getClearRow());
        assertEquals(2, downData.getClearRow().getLinesRemoved());
        assertEquals(206, board.getScore().scoreProperty().get(),
                "Three-cell hard drop (3*2) plus double clear (200) should total 206 points");
    }

    @Test
    void hardDropAfterRotationCountsAsTSpin() {
        StubBoard board = new StubBoard();
        board.boardMatrix = new int[][]{
                {9, 9, 9, 0},
                {0, 0, 0, 0},
                {9, 0, 0, 0},
                {0, 0, 0, 0}
        };
        board.nextClear = new ClearRow(2, board.boardMatrix, 0);
        board.viewData = new ViewData(new int[][]{{1}}, 0, 0, 0, Collections.emptyList(), 0);
        board.tetrominoType = TetrominoType.T;

        GameLogic logic = new GameLogic(board);
        logic.rotateClockwise();

        logic.hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));

        assertEquals(200, board.getScore().scoreProperty().get(),
                "Rotation followed by hard drop should keep the rotation flag for T-Spin scoring");
    }

    private static final class StubBoard implements Board {

        private int downMovesRemaining;
        private boolean rotateSuccess = true;
        private boolean newBrickConflict = false;
        private int[][] boardMatrix = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        private ViewData viewData = new ViewData(new int[][]{{0}}, 0, 0, 0, Collections.emptyList(), 0);
        private TetrominoType tetrominoType = TetrominoType.O;
        private ClearRow nextClear = new ClearRow(0, MatrixOperations.copy(boardMatrix), 0);
        private final Score score = new Score();

        @Override
        public boolean moveBrickDown() {
            if (downMovesRemaining > 0) {
                downMovesRemaining--;
                return true;
            }
            return false;
        }

        @Override
        public boolean moveBrickLeft() {
            return false;
        }

        @Override
        public boolean moveBrickRight() {
            return false;
        }

        @Override
        public boolean rotateClockwise() {
            return rotateSuccess;
        }

        @Override
        public boolean rotateCounterClockwise() {
            return rotateSuccess;
        }

        @Override
        public boolean createNewBrick() {
            return newBrickConflict;
        }

        @Override
        public int[][] getBoardMatrix() {
            return MatrixOperations.copy(boardMatrix);
        }

        @Override
        public ViewData getViewData() {
            return viewData;
        }

        @Override
        public void mergeBrickToBackground() {
        }

        @Override
        public ClearRow clearRows() {
            return nextClear;
        }

        @Override
        public Score getScore() {
            return score;
        }

        @Override
        public void newGame() {
        }

        @Override
        public TetrominoType getActiveTetrominoType() {
            return tetrominoType;
        }

        @Override
        public int calculateDropDistance() {
            return downMovesRemaining;
        }
    }
}
