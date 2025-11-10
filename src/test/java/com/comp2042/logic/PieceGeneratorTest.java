package com.comp2042.logic;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.PieceGenerator;
import com.comp2042.logic.bricks.TetrominoType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PieceGeneratorTest {

    @Test
    void eachBagContainsEveryTetromino() {
        PieceGenerator generator = new PieceGenerator();
        Set<TetrominoType> bag = EnumSet.noneOf(TetrominoType.class);
        for (int i = 0; i < 7; i++) {
            bag.add(generator.getBrick().getType());
        }
        assertEquals(EnumSet.allOf(TetrominoType.class), bag);
    }

    @Test
    void bagsRepeatFullSetAfterDepletion() {
        PieceGenerator generator = new PieceGenerator();
        // Deplete first bag
        for (int i = 0; i < 7; i++) {
            generator.getBrick();
        }
        // Second bag should again contain every tetromino
        Set<TetrominoType> bag = EnumSet.noneOf(TetrominoType.class);
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            bag.add(brick.getType());
        }
        assertEquals(EnumSet.allOf(TetrominoType.class), bag);
    }

    @Test
    void peekUpcomingReturnsFiveWithoutConsumingQueue() {
        PieceGenerator generator = new PieceGenerator();
        List<Brick> preview = generator.peekUpcoming(5);
        assertEquals(5, preview.size());
        Brick nextFromPeek = preview.get(0);
        Brick actualNext = generator.getBrick();
        assertEquals(nextFromPeek.getType(), actualNext.getType());
    }
}
