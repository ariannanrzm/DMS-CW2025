package com.comp2042.game.bricks;

import java.util.List;

/**
 * Interface defining the strategy for generating new bricks (Tetrominoes).
 * Implementations can provide different generation algorithms (e.g., completely random,
 * "bag" system, or predetermined sequences for testing).
 */
public interface BrickGenerator {

    /**
     * Retrieves the next brick in the sequence and removes it from the generator's queue.
     * Called when spawning a new piece onto the board.
     *
     * @return The next {@link Brick} to be played.
     */
    Brick getBrick();

    /**
     * Peeks at the immediate next brick without removing it from the sequence.
     * Useful for displaying the single "Next Piece" preview.
     *
     * @return The upcoming {@link Brick} that will be generated next.
     */
    Brick getNextBrick();

    /**
     * Retrieves a list of upcoming bricks without altering the generator's state.
     * Used for displaying a multi-piece preview (e.g., the next 3 pieces).
     *
     * @param count The number of upcoming bricks to retrieve.
     * @return A list containing the next {@code count} bricks.
     */
    List<Brick> getNextBricks(int count);
}
