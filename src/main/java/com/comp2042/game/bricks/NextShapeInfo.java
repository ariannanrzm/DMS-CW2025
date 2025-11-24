package com.comp2042.game.bricks;


import com.comp2042.util.ArrayOperations;

public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    public int[][] getShape() {
        return ArrayOperations.copy(shape);
    }

    public int getPosition() {
        return position;
    }
}
