package com.comp2042.board;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.TetrominoType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {

    @Test
    void createNewBrickSpawnsWithoutCollision() {
        SimpleBoard board = new SimpleBoard(25, 10);
        boolean conflict = board.createNewBrick();
        assertFalse(conflict, "First spawn should not collide with background");
        ViewData viewData = board.getViewData();
        assertNotNull(viewData.getBrickData());
    }

    @Test
    void clearRowsRemovesFilledLines() {
        SimpleBoard board = new SimpleBoard(25, 10);
        board.createNewBrick();
        int[][] matrix = board.getBoardMatrix();
        int bottomRow = matrix.length - 1;
        for (int col = 0; col < matrix[0].length; col++) {
            matrix[bottomRow][col] = 1;
        }
        ClearRow clearRow = board.clearRows();
        assertEquals(1, clearRow.getLinesRemoved());
        assertArrayEquals(new int[matrix[0].length], board.getBoardMatrix()[bottomRow]);
    }

    @Test
    void calculateDropDistanceStopsAboveStack() {
        SimpleBoard board = new SimpleBoard(6, 6, new SingleCellGenerator());
        board.createNewBrick();
        ViewData viewData = board.getViewData();
        // Move the brick into the field so drop distance calculations are relative to row 0
        board.moveBrickDown();
        int spawnColumn = viewData.getxPosition();
        board.getBoardMatrix()[2][spawnColumn] = 9;

        int distance = board.calculateDropDistance();

        assertEquals(1, distance, "Brick should only drop until the row above the stack");
    }

    @Test
    void mergeBrickWritesToMatrix() {
        SimpleBoard board = new SimpleBoard(6, 6, new SingleCellGenerator());
        board.createNewBrick();
        board.moveBrickDown();
        ViewData viewData = board.getViewData();

        board.mergeBrickToBackground();

        assertTrue(board.getBoardMatrix()[viewData.getyPosition()][viewData.getxPosition()] > 0);
    }

    @Test
    void newGameResetsMatrixAndScore() {
        SimpleBoard board = new SimpleBoard(6, 6, new SingleCellGenerator());
        board.createNewBrick();
        board.getBoardMatrix()[0][0] = 7;
        board.getScore().add(500);

        board.newGame();

        for (int[] row : board.getBoardMatrix()) {
            for (int cell : row) {
                assertEquals(0, cell);
            }
        }
        assertEquals(0, board.getScore().scoreProperty().get());
    }

    private static final class SingleCellGenerator implements BrickGenerator {

        private final Brick brick = new SingleCellBrick();

        @Override
        public Brick getBrick() {
            return brick;
        }

        @Override
        public Brick getNextBrick() {
            return brick;
        }

        @Override
        public List<Brick> peekUpcoming(int count) {
            return Collections.nCopies(count, brick);
        }
    }

    private static final class SingleCellBrick implements Brick {

        private final List<int[][]> rotations = List.of(new int[][]{
                {1}
        });

        @Override
        public List<int[][]> getShapeMatrix() {
            return rotations;
        }

        @Override
        public TetrominoType getType() {
            return TetrominoType.O;
        }
    }
}
