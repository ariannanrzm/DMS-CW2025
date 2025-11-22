package com.comp2042;

import com.comp2042.logic.bricks.Brick;

/**
 * The BrickRotator class manages the rotation state of the active Tetris brick.
 * Each brick contains multiple rotation matrices. This class controls which
 * rotation is currently in use and computes subsequent rotations.
 *
 * This refactored version improves naming clarity and adds documentation
 * without altering the original rotation behaviour.
 */
public class BrickRotator {

    /** The currently active brick whose rotations are being managed. */
    private Brick brick;

    /** Index representing the current rotation state of the brick. */
    private int currentShape = 0;

    /**
     * Computes (but does not apply) the next rotation state of the current brick.
     * Rotation cycles through the available rotation matrices using modular arithmetic.
     *
     * @return NextShapeInfo containing the next rotation matrix and its index
     */
    public NextShapeInfo getNextShape() {
        int nextShape = (currentShape + 1) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Returns the currently active rotation matrix of the brick.
     *
     * @return a 2D array representing the current rotation of the brick
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Applies a rotation by updating the active rotation index.
     * This method does not compute or validate rotation correctness —
     * validation must be handled externally
     *
     * @param rotationIndex the index of the rotation to apply
     */
    public void applyRotation(int rotationIndex) {
        this.currentShape = rotationIndex;
    }

    /**
     * Sets the currently active brick and resets its rotation to the default state.
     *
     * @param brick the new brick instance to rotate
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
}
