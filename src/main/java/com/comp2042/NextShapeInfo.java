package com.comp2042;

import java.awt.Point;

public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;
    private final Point[] kicks;

    public NextShapeInfo(final int[][] shape, final int position, final Point[] kicks) {
        this.shape = shape;
        this.position = position;
        this.kicks = kicks.clone();
    }

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    public int getPosition() {
        return position;
    }

    public Point[] getKicks() {
        Point[] copy = new Point[kicks.length];
        for (int i = 0; i < kicks.length; i++) {
            Point source = kicks[i];
            copy[i] = new Point(source);
        }
        return copy;
    }
}
