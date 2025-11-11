package com.comp2042;

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

    com.comp2042.logic.bricks.TetrominoType getActiveTetrominoType();

    int calculateDropDistance();
}
