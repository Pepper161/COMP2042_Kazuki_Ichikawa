package com.comp2042;

import com.comp2042.board.ClearRow;
import com.comp2042.board.MatrixOperations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertArrayEquals(new int[]{0, 2, 0, 0}, newMatrix[2]);
        assertArrayEquals(new int[]{3, 3, 3, 3}, newMatrix[3]);
    }
}
