package com.comp2042.board;

import com.comp2042.ClearRow;
import com.comp2042.SimpleBoard;
import com.comp2042.ViewData;
import org.junit.jupiter.api.Test;

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
}
