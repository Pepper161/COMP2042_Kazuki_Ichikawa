package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Implements the Guideline 7-bag randomiser. Every bag contains one instance of
 * each Tetromino; the bag is shuffled and consumed before a new bag is generated.
 */
public class PieceGenerator implements BrickGenerator {

    private final Deque<Brick> queue = new ArrayDeque<>();

    public PieceGenerator() {
        refillBag();
    }

    @Override
    public Brick getBrick() {
        if (queue.isEmpty()) {
            refillBag();
        }
        Brick next = queue.poll();
        if (queue.isEmpty()) {
            refillBag();
        }
        return next;
    }

    @Override
    public Brick getNextBrick() {
        if (queue.isEmpty()) {
            refillBag();
        }
        return queue.peek();
    }

    @Override
    public List<Brick> peekUpcoming(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        ensureQueueSize(count);
        List<Brick> preview = new ArrayList<>(count);
        Iterator<Brick> iterator = queue.iterator();
        for (int i = 0; i < count && iterator.hasNext(); i++) {
            preview.add(iterator.next());
        }
        return preview;
    }

    private void ensureQueueSize(int minSize) {
        while (queue.size() < minSize) {
            refillBag();
        }
    }

    private void refillBag() {
        List<Brick> bag = new ArrayList<>(7);
        bag.add(new IBrick());
        bag.add(new JBrick());
        bag.add(new LBrick());
        bag.add(new OBrick());
        bag.add(new SBrick());
        bag.add(new TBrick());
        bag.add(new ZBrick());
        Collections.shuffle(bag);
        queue.addAll(bag);
    }
}
