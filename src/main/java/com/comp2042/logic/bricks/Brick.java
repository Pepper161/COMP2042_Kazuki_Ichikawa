package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Represents a tetromino piece (brick) with a specific shape and type.
 */
public interface Brick {

    /**
     * Returns the list of rotation states for this brick.
     * Each state is a 2D integer array representing the shape.
     *
     * @return the list of shape matrices
     */
    List<int[][]> getShapeMatrix();

    /**
     * Returns the type of this tetromino.
     *
     * @return the tetromino type
     */
    TetrominoType getType();
}
