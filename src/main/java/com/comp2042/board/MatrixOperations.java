package com.comp2042.board;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixOperations {


    //We don't want to instantiate this utility class
    private MatrixOperations(){

    }

    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    if (targetY < 0) {
                        continue;
                    }
                    if (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0) {
                        return true;
                    }
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
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    if (targetY < 0) {
                        continue;
                    }
                    copy[targetY][targetX] = brick[j][i];
                }
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
