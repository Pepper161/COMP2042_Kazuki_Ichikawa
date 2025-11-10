package com.comp2042;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> nextBrickData;
    private final int gameOverRow;

    public ViewData(int[][] brickData, int xPosition, int yPosition, List<int[][]> nextBrickData, int gameOverRow) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData == null ? Collections.emptyList() : nextBrickData;
        this.gameOverRow = gameOverRow;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public List<int[][]> getNextBricksData() {
        return nextBrickData.stream()
                .map(MatrixOperations::copy)
                .collect(Collectors.toList());
    }

    public int getGameOverRow() {
        return gameOverRow;
    }
}
