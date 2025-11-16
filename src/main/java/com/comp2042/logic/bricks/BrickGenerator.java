package com.comp2042.logic.bricks;

import java.util.List;

public interface BrickGenerator {

    Brick getBrick();

    Brick getNextBrick();

    List<Brick> peekUpcoming(int count);

    default void reset() {
        // Generators that support resetting can override.
    }
}
