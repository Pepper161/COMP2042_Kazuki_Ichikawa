package com.comp2042;

import com.comp2042.board.ClearRow;
import com.comp2042.board.MatrixOperations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatrixOperationsTest {

    @Test
    void clearingRowsPullsAboveBlocksDown() {
        int[][] board = {
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 2, 0, 0},
                {3, 3, 3, 3}
        };
        ClearRow result = MatrixOperations.checkRemoving(board);
        assertEquals(2, result.getLinesRemoved());
        int[][] newMatrix = result.getNewMatrix();
        assertArrayEquals(new int[]{0, 0, 0, 0}, newMatrix[0]);
        assertArrayEquals(new int[]{0, 0, 0, 0}, newMatrix[1]);
        assertArrayEquals(new int[]{0, 0, 0, 0}, newMatrix[2]);
        assertArrayEquals(new int[]{0, 2, 0, 0}, newMatrix[3]);
    }

    @Test
    void rectangularBricksDoNotCauseIntersectionErrors() {
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        int[][] lShape = {
                {1, 0},
                {1, 1},
                {0, 1}
        };
        boolean conflict = MatrixOperations.intersect(board, lShape, 0, 0);
        assertFalse(conflict);

        board[1][0] = 9;
        assertTrue(MatrixOperations.intersect(board, lShape, 0, 0));

        int[][] merged = MatrixOperations.merge(board, lShape, 2, 0);
        assertEquals(1, merged[0][2]);
        assertEquals(1, merged[1][2]);
        assertEquals(1, merged[2][3]);
    }
}
