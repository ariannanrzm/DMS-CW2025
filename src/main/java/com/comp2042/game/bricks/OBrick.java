package com.comp2042.game.bricks;

/**
 * Represents the "O" shaped brick (Tetromino)
 */
public final class OBrick extends AbstractBrick {
    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }
}