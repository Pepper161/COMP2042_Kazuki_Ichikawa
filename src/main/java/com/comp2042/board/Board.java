package com.comp2042.board;

import com.comp2042.game.Score;
import com.comp2042.logic.bricks.TetrominoType;

/**
 * Abstraction over the playfield state that exposes the operations required by
 * {@link com.comp2042.game.GameLogic}.
 */
public interface Board {

    /**
     * Moves the active brick down by one row.
     *
     * @return true if the move was successful, false if blocked
     */
    boolean moveBrickDown();

    /**
     * Moves the active brick left by one column.
     *
     * @return true if the move was successful, false if blocked
     */
    boolean moveBrickLeft();

    /**
     * Moves the active brick right by one column.
     *
     * @return true if the move was successful, false if blocked
     */
    boolean moveBrickRight();

    /**
     * Rotates the active brick 90 degrees clockwise.
     *
     * @return true if the rotation was successful, false if blocked
     */
    boolean rotateClockwise();

    /**
     * Rotates the active brick 90 degrees counter-clockwise.
     *
     * @return true if the rotation was successful, false if blocked
     */
    boolean rotateCounterClockwise();

    /**
     * Spawns a new active brick at the top of the board.
     *
     * @return true if the spawn was successful, false if blocked (game over
     *         condition)
     */
    boolean createNewBrick();

    /**
     * Returns the current state of the board grid.
     *
     * @return a 2D array representing the board, where non-zero values indicate
     *         occupied cells
     */
    int[][] getBoardMatrix();

    /**
     * Returns a view object containing data required for rendering.
     *
     * @return the current view data
     */
    ViewData getViewData();

    /**
     * Locks the active brick into the background grid.
     */
    void mergeBrickToBackground();

    /**
     * Checks for and clears any full rows.
     *
     * @return an object detailing which rows were cleared and the score awarded
     */
    ClearRow clearRows();

    /**
     * Returns the current score object.
     *
     * @return the score tracker
     */
    Score getScore();

    /**
     * Resets the board state for a new game.
     */
    void newGame();

    /**
     * Returns the type of the currently active tetromino.
     *
     * @return the active tetromino type
     */
    TetrominoType getActiveTetrominoType();

    /**
     * Calculates the distance the active brick can drop before hitting an obstacle.
     *
     * @return the drop distance in rows
     */
    int calculateDropDistance();
}
