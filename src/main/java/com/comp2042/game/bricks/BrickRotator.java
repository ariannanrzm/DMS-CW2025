package com.comp2042.game.bricks;

import com.comp2042.game.board.GamePoint;
import com.comp2042.util.MatrixOperations;

/**
 * Manages the rotation logic for bricks.
 * It handles the retrieval of rotation matrices and implements logic to attempt
 * rotations with wall-kick offsets if the standard rotation is blocked.
 */
public class BrickRotator {

    private Brick brick;

    private int currentShape = 0;


    /**
     * Attempts to rotate the current brick.
     * Tries the standard rotation first, then applies wall kicks (shifts) if needed.
     *
     * @param boardMatrix The current state of the board grid (for collision checks)
     * @param currentOffset The current x, y coordinates of the brick
     * @return The new valid GamePoint if rotation succeeded, or null if impossible.
     */
    public GamePoint tryRotate(int[][] boardMatrix, GamePoint currentOffset) {
        NextShapeInfo nextShapeInfo = getNextShape();
        int[][] nextMatrix = nextShapeInfo.getShape();

        int currentX = currentOffset.x();
        int currentY = currentOffset.y();

        int[] kickOffsets = {0, 1, -1, 2, -2};

        for (int kick : kickOffsets) {
            int targetX = currentX + kick;

            if (!MatrixOperations.intersect(boardMatrix, nextMatrix, targetX, currentY)) {
                applyRotation(nextShapeInfo.getPosition());
                return currentOffset.translate(kick, 0);
            }
        }

        return null;
    }

    /**
     * Computes the next rotation state of the current brick.
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
