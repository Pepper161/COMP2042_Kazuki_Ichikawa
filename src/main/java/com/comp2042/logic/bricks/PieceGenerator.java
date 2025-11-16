package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements the Guideline 7-bag randomiser. Every bag contains one instance of
 * each Tetromino; the bag is shuffled and consumed before a new bag is generated.
 */
public class PieceGenerator implements BrickGenerator {

    private final Deque<Brick> queue = new ArrayDeque<>();
    private final Random random = new Random();
    private final Long fixedSeed;
    private long activeSeed;

    public PieceGenerator() {
        this.fixedSeed = null;
        reseed(ThreadLocalRandom.current().nextLong());
    }

    public PieceGenerator(long seed) {
        this.fixedSeed = seed;
        reseed(seed);
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

    @Override
    public void reset() {
        if (fixedSeed != null) {
            reseed(fixedSeed);
        } else {
            reseed(ThreadLocalRandom.current().nextLong());
        }
    }

    public long getCurrentSeed() {
        return activeSeed;
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
        Collections.shuffle(bag, random);
        queue.addAll(bag);
    }

    private void reseed(long seed) {
        activeSeed = seed;
        random.setSeed(seed);
        queue.clear();
        refillBag();
    }
}
