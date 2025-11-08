package com.comp2042.logic.bricks;

import com.comp2042.BrickRotator;
import com.comp2042.NextShapeInfo;
import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BrickRotatorTest {

    @Test
    void standardPiecesUseStandardKickTable() {
        BrickRotator rotator = new BrickRotator();
        rotator.setBrick(new TBrick());
        NextShapeInfo nextShape = rotator.getNextShape();
        Point[] kicks = nextShape.getKicks();
        assertEquals(5, kicks.length);
        assertEquals(new Point(0, 0), kicks[0]);
        assertEquals(new Point(-1, 0), kicks[1]);
        assertEquals(new Point(-1, 1), kicks[2]);
    }

    @Test
    void iPieceUsesIKickTable() {
        BrickRotator rotator = new BrickRotator();
        rotator.setBrick(new IBrick());
        NextShapeInfo nextShape = rotator.getNextShape();
        Point[] kicks = nextShape.getKicks();
        assertEquals(5, kicks.length);
        assertEquals(new Point(0, 0), kicks[0]);
        assertEquals(new Point(-2, 0), kicks[1]);
        assertEquals(new Point(1, 0), kicks[2]);
    }
}

