package com.comp2042.game.board;

import com.comp2042.util.ArrayOperations;

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;

    public ClearRow(int linesRemoved, int[][] newMatrix) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = ArrayOperations.copy(newMatrix);
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return ArrayOperations.copy(newMatrix);
    }

}
