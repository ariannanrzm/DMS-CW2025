package com.comp2042.game.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Implementation of BrickGenerator that provides a stream of random bricks.
 * Maintains a buffer of upcoming bricks to allow the "Next Brick" preview feature.
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private static final int BUFFER_SIZE = 5;

    /**
     * Constructs a new RandomBrickGenerator.
     * Initializes the list of available brick types (I, J, L, O, S, T, Z) and pre-fills the buffer.
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());

        ensureBuffer();
    }

    /**
     * Ensures that the internal buffer of upcoming bricks has enough elements.
     * If buffer size drops below the threshold, new random bricks are generated and added.
     */
    private void ensureBuffer() {
        while (nextBricks.size() < BUFFER_SIZE) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
    }

    /**
     * Retrieves and removes the next brick from the buffer.
     * This is called when a new piece needs to spawn on the board.
     *
     * @return The next {@link Brick} to use in the game.
     */
    @Override
    public Brick getBrick() {
        ensureBuffer();
        return nextBricks.poll();
    }

    /**
     * Peeks at the immediate next brick in the buffer without removing it.
     * Useful for displaying the primary "Next Piece" preview.
     *
     * @return The upcoming {@link Brick}.
     */
    @Override
    public Brick getNextBrick() {
        ensureBuffer();
        return nextBricks.peek();
    }

    /**
     * Retrieves a list of upcoming bricks from the buffer without modifying the generator's state.
     * Used for displaying multiple future pieces in the UI.
     *
     * @param count The number of upcoming bricks to retrieve.
     * @return A list containing the next {@code count} bricks.
     */
    @Override
    public List<Brick> getNextBricks(int count) {
        ensureBuffer();
        return nextBricks.stream().limit(count).collect(Collectors.toList());
    }

}
