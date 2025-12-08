package com.comp2042.game.bricks;

import java.util.List;

/**
 * Interface representing a Tetris game piece (Tetromino).
 * Defines the contract for bricks to provide their shape configurations
 * and rotation states to the game board.
 */
public interface Brick {

    /**
     * Retrieves the list of rotation matrices for this brick.
     *
     * @return A list of 2D integer arrays representing the brick's shape at different rotations.
     */
    List<int[][]> getShapeMatrix();
}
