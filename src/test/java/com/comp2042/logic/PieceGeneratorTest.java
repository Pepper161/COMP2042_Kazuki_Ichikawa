package com.comp2042.logic;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.PieceGenerator;
import com.comp2042.logic.bricks.TetrominoType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

    @Test
    void deterministicSeedProducesRepeatableSequence() {
        PieceGenerator seededA = new PieceGenerator(1234L);
        PieceGenerator seededB = new PieceGenerator(1234L);
        TetrominoType[] sequenceA = captureSequence(seededA, 21);
        TetrominoType[] sequenceB = captureSequence(seededB, 21);
        assertArrayEquals(sequenceA, sequenceB);
    }

    @Test
    void resetWithFixedSeedReplaysSameSequence() {
        PieceGenerator seeded = new PieceGenerator(777L);
        TetrominoType[] initial = captureSequence(seeded, 14);
        seeded.reset();
        TetrominoType[] replay = captureSequence(seeded, 14);
        assertArrayEquals(initial, replay);
    }

    @Test
    void resetWithoutSeedProducesNewRandomSeed() {
        PieceGenerator generator = new PieceGenerator();
        long firstSeed = generator.getCurrentSeed();
        generator.reset();
        long secondSeed = generator.getCurrentSeed();
        assertNotEquals(firstSeed, secondSeed, "Random runs should publish new seeds after reset");
    }

    private TetrominoType[] captureSequence(PieceGenerator generator, int count) {
        TetrominoType[] values = new TetrominoType[count];
        for (int i = 0; i < count; i++) {
            values[i] = generator.getBrick().getType();
        }
        return values;
    }
}
