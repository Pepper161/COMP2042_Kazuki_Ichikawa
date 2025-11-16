package com.comp2042.board;

import com.comp2042.game.Score;
import com.comp2042.logic.bricks.TetrominoType;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateClockwise();

    boolean rotateCounterClockwise();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    TetrominoType getActiveTetrominoType();

    int calculateDropDistance();
}
