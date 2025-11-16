package com.comp2042.board;

import com.comp2042.game.Score;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.PieceGenerator;
import com.comp2042.logic.bricks.TetrominoType;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default {@link Board} implementation that holds the matrix state, active tetromino,
 * and helper objects (rotator/generator) needed for Guideline playfields.
 */
public class SimpleBoard implements Board {

    public static final int SPAWN_ROW = -1;
    public static final int GAME_OVER_ROW = 0;
    private static final int NEXT_PREVIEW_COUNT = 5;

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this(width, height, new PieceGenerator());
    }

    public SimpleBoard(int width, int height, BrickGenerator brickGenerator) {
        this.width = width;
        this.height = height;
        this.brickGenerator = brickGenerator != null ? brickGenerator : new PieceGenerator();
        this.currentGameMatrix = new int[width][height];
        this.brickRotator = new BrickRotator();
        this.score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateClockwise() {
        return attemptRotation(brickRotator.getNextShapeClockwise());
    }

    @Override
    public boolean rotateCounterClockwise() {
        return attemptRotation(brickRotator.getNextShapeCounterClockwise());
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        int spawnColumn = Math.max(0, (currentGameMatrix[0].length / 2) - 1);
        currentOffset = new Point(spawnColumn, SPAWN_ROW);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        int ghostY = (int) currentOffset.getY() + calculateDropDistance();
        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                ghostY,
                collectNextPreview(),
                GAME_OVER_ROW);
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        brickGenerator.reset();
        createNewBrick();
    }

    @Override
    public TetrominoType getActiveTetrominoType() {
        return brickRotator.getType();
    }

    @Override
    public int calculateDropDistance() {
        if (currentOffset == null) {
            return 0;
        }
        Point probe = new Point(currentOffset);
        int distance = 0;
        while (true) {
            probe.translate(0, 1);
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), probe);
            if (conflict) {
                break;
            }
            distance++;
        }
        return distance;
    }

    private List<int[][]> collectNextPreview() {
        return brickGenerator.peekUpcoming(NEXT_PREVIEW_COUNT)
                .stream()
                .map(brick -> {
                    List<int[][]> rotations = brick.getShapeMatrix();
                    if (rotations.isEmpty()) {
                        return new int[0][0];
                    }
                    return MatrixOperations.copy(rotations.get(0));
                })
                .collect(Collectors.toList());
    }

    private boolean attemptRotation(NextShapeInfo rotationInfo) {
        int[][] rotatedShape = rotationInfo.getShape();
        Point[] kicks = rotationInfo.getKicks();
        for (Point kick : kicks) {
            Point candidateOffset = new Point(currentOffset);
            candidateOffset.translate((int) kick.getX(), (int) kick.getY());
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, rotatedShape, candidateOffset);
            if (!conflict) {
                brickRotator.setCurrentShape(rotationInfo.getPosition());
                currentOffset = candidateOffset;
                return true;
            }
        }
        return false;
    }
}
