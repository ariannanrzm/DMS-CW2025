package com.comp2042.game.bricks;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "I" shaped brick (Tetromino)
 */
public final class IBrick extends AbstractBrick {

    public IBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }
}

