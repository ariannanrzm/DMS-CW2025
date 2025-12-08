package com.comp2042.game.bricks;

import com.comp2042.util.ArrayOperations;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all bricks.
 * Handles the storage and retrieval of shape matrices to reduce duplication.
 */
public abstract class AbstractBrick implements Brick {

    protected final List<int[][]> brickMatrix = new ArrayList<>();

    @Override
    public List<int[][]> getShapeMatrix() {
        return ArrayOperations.deepCopyList(brickMatrix);
    }
}