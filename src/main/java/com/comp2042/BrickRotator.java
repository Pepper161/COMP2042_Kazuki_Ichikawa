package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.TetrominoType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BrickRotator {

    private static final int ORIENTATION_COUNT = 4;
    private static final Point[][][] STANDARD_KICKS = new Point[ORIENTATION_COUNT][ORIENTATION_COUNT][];
    private static final Point[][][] I_KICKS = new Point[ORIENTATION_COUNT][ORIENTATION_COUNT][];

    static {
        registerStandard(0, 1, points(
                0, 0,
                -1, 0,
                -1, 1,
                0, -2,
                -1, -2
        ));
        registerStandard(1, 0, points(
                0, 0,
                1, 0,
                1, -1,
                0, 2,
                1, 2
        ));
        registerStandard(1, 2, points(
                0, 0,
                1, 0,
                1, -1,
                0, 2,
                1, 2
        ));
        registerStandard(2, 1, points(
                0, 0,
                -1, 0,
                -1, 1,
                0, -2,
                -1, -2
        ));
        registerStandard(2, 3, points(
                0, 0,
                1, 0,
                1, 1,
                0, -2,
                1, -2
        ));
        registerStandard(3, 2, points(
                0, 0,
                -1, 0,
                -1, -1,
                0, 2,
                -1, 2
        ));
        registerStandard(3, 0, points(
                0, 0,
                -1, 0,
                -1, -1,
                0, 2,
                -1, 2
        ));
        registerStandard(0, 3, points(
                0, 0,
                1, 0,
                1, 1,
                0, -2,
                1, -2
        ));

        registerIKicks(0, 1, points(
                0, 0,
                -2, 0,
                1, 0,
                -2, -1,
                1, 2
        ));
        registerIKicks(1, 0, points(
                0, 0,
                2, 0,
                -1, 0,
                2, 1,
                -1, -2
        ));
        registerIKicks(1, 2, points(
                0, 0,
                -1, 0,
                2, 0,
                -1, 2,
                2, -1
        ));
        registerIKicks(2, 1, points(
                0, 0,
                1, 0,
                -2, 0,
                1, -2,
                -2, 1
        ));
        registerIKicks(2, 3, points(
                0, 0,
                2, 0,
                -1, 0,
                2, 1,
                -1, -2
        ));
        registerIKicks(3, 2, points(
                0, 0,
                -2, 0,
                1, 0,
                -2, -1,
                1, 2
        ));
        registerIKicks(3, 0, points(
                0, 0,
                1, 0,
                -2, 0,
                1, -2,
                -2, 1
        ));
        registerIKicks(0, 3, points(
                0, 0,
                -1, 0,
                2, 0,
                -1, 2,
                2, -1
        ));
    }

    private List<int[][]> rotations = new ArrayList<>();
    private TetrominoType type = TetrominoType.O;
    private int currentShape = 0;

    public NextShapeInfo getNextShape() {
        int from = currentShape;
        int to = (currentShape + 1) % ORIENTATION_COUNT;
        int[][] rotatedShape = MatrixOperations.copy(getShapeForOrientation(to));
        Point[] kicks = getKickData(from, to);
        return new NextShapeInfo(rotatedShape, to, kicks);
    }

    public int[][] getCurrentShape() {
        return MatrixOperations.copy(getShapeForOrientation(currentShape));
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = ((currentShape % ORIENTATION_COUNT) + ORIENTATION_COUNT) % ORIENTATION_COUNT;
    }

    public void setBrick(Brick brick) {
        this.rotations = brick.getShapeMatrix();
        this.type = brick.getType();
        currentShape = 0;
    }

    private int[][] getShapeForOrientation(int orientation) {
        if (rotations.isEmpty()) {
            return new int[0][0];
        }
        int index = Math.floorMod(orientation, rotations.size());
        return rotations.get(index);
    }

    private Point[] getKickData(int from, int to) {
        Point[][][] table = type == TetrominoType.I ? I_KICKS : STANDARD_KICKS;
        Point[] result = table[from][to];
        if (result == null) {
            return new Point[]{new Point(0, 0)};
        }
        return result;
    }

    private static void registerStandard(int from, int to, Point[] kicks) {
        STANDARD_KICKS[from][to] = kicks;
    }

    private static void registerIKicks(int from, int to, Point[] kicks) {
        I_KICKS[from][to] = kicks;
    }

    private static Point[] points(int... coords) {
        Point[] result = new Point[coords.length / 2];
        for (int i = 0; i < coords.length; i += 2) {
            result[i / 2] = new Point(coords[i], coords[i + 1]);
        }
        return result;
    }
}
