package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Displays the Guideline-style NEXT queue (five previews) beside the board.
 * Nodes are created once and re-used to avoid frame drops when a new bag is drawn.
 */
public class NextQueuePanel extends VBox {

    private static final int MAX_QUEUE = 5;
    private static final int CELL_SIZE = 16;
    private static final int SLOT_ROWS = 4;
    private static final int SLOT_COLS = 4;

    private final VBox queueContainer = new VBox(8);
    private final QueueSlot[] slots = new QueueSlot[MAX_QUEUE];

    public NextQueuePanel() {
        setSpacing(6);
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("next-queue-panel");

        Label title = new Label("NEXT");
        title.getStyleClass().add("next-queue-title");

        queueContainer.setAlignment(Pos.TOP_CENTER);
        queueContainer.getStyleClass().add("next-queue-container");

        for (int i = 0; i < MAX_QUEUE; i++) {
            slots[i] = createSlot();
            queueContainer.getChildren().add(slots[i].grid);
        }

        getChildren().addAll(title, queueContainer);
    }

    public void setQueue(List<int[][]> nextBricks) {
        for (int i = 0; i < slots.length; i++) {
            int[][] data = (nextBricks != null && i < nextBricks.size()) ? nextBricks.get(i) : null;
            slots[i].apply(data);
        }
    }

    private QueueSlot createSlot() {
        GridPane grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setAlignment(Pos.CENTER);
        Rectangle[][] cells = new Rectangle[SLOT_ROWS][SLOT_COLS];
        for (int row = 0; row < SLOT_ROWS; row++) {
            for (int col = 0; col < SLOT_COLS; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.getStyleClass().add("next-queue-cell");
                cell.setArcHeight(8);
                cell.setArcWidth(8);
                cell.setFill(BrickColorPalette.resolve(0));
                cells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }
        return new QueueSlot(grid, cells);
    }

    private static final class QueueSlot {
        private final GridPane grid;
        private final Rectangle[][] cells;

        private QueueSlot(GridPane grid, Rectangle[][] cells) {
            this.grid = grid;
            this.cells = cells;
        }

        private void apply(int[][] matrix) {
            for (int row = 0; row < cells.length; row++) {
                for (int col = 0; col < cells[row].length; col++) {
                    int value = 0;
                    if (matrix != null
                            && row < matrix.length
                            && col < matrix[row].length) {
                        value = matrix[row][col];
                    }
                    cells[row][col].setFill(BrickColorPalette.resolve(value));
                }
            }
        }
    }
}
