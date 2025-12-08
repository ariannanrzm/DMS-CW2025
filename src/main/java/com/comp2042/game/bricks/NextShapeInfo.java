package com.comp2042.game.bricks;

import com.comp2042.util.ArrayOperations;

/**
 * A simple data carrier used by the BrickRotator to pass rotation information.
 * Holds the matrix for the next potential rotation state and its corresponding index.
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a new NextShapeInfo instance.
     *
     * @param shape    The 2D matrix representing the next rotation shape.
     * @param position The index/state identifier of this rotation.
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Retrieves the shape matrix for the next rotation.
     * Returns a defensive copy to prevent external modification.
     *
     * @return A 2D integer array representing the rotated shape.
     */
    public int[][] getShape() {
        return ArrayOperations.copy(shape);
    }

    /**
     * Retrieves the index associated with this rotation state.
     *
     * @return The rotation position index.
     */
    public int getPosition() {
        return position;
    }
}
