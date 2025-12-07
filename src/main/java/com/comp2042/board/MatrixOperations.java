package com.comp2042.board;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility methods for copying, merging, and clearing board matrices.
 */
public class MatrixOperations {

    private MatrixOperations() {
    }

    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        if (brick == null) {
            return false;
        }
        for (int row = 0; row < brick.length; row++) {
            int[] brickRow = brick[row];
            if (brickRow == null) {
                continue;
            }
            for (int col = 0; col < brickRow.length; col++) {
                int cell = brickRow[col];
                if (cell == 0) {
                    continue;
                }
                int targetX = x + col;
                int targetY = y + row;
                if (targetY < 0) {
                    continue;
                }
                if (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean intersect(final int[][] matrix, final int[][] brick, Point offset) {
        return intersect(matrix, brick, (int) offset.getX(), (int) offset.getY());
    }

    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        if (targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length) {
            returnValue = false;
        }
        return returnValue;
    }

    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        if (brick == null) {
            return copy;
        }
        for (int row = 0; row < brick.length; row++) {
            int[] brickRow = brick[row];
            if (brickRow == null) {
                continue;
            }
            for (int col = 0; col < brickRow.length; col++) {
                int cell = brickRow[col];
                if (cell == 0) {
                    continue;
                }
                int targetX = x + col;
                int targetY = y + row;
                if (targetY < 0) {
                    continue;
                }
                copy[targetY][targetX] = cell;
            }
        }
        return copy;
    }

    public static ClearRow checkRemoving(final int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] tmp = new int[rows][cols];
        int writeRow = rows - 1;
        int cleared = 0;

        for (int source = rows - 1; source >= 0; source--) {
            boolean rowToClear = true;
            for (int col = 0; col < cols; col++) {
                if (matrix[source][col] == 0) {
                    rowToClear = false;
                    break;
                }
            }
            if (rowToClear) {
                cleared++;
            } else {
                System.arraycopy(matrix[source], 0, tmp[writeRow], 0, cols);
                writeRow--;
            }
        }

        while (writeRow >= 0) {
            Arrays.fill(tmp[writeRow], 0);
            writeRow--;
        }

        int scoreBonus = 50 * cleared * cleared;
        return new ClearRow(cleared, tmp, scoreBonus);
    }

    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
