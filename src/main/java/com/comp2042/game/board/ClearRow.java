package com.comp2042.game.board;

import com.comp2042.util.ArrayOperations;

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Full constructor including score.
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = ArrayOperations.copy(newMatrix);
        this.scoreBonus = scoreBonus;
    }

    /**
     * Compatibility constructor (defaults score to 0).
     * Used by MatrixOperations or tests that don't care about score.
     */
    public ClearRow(int linesRemoved, int[][] newMatrix) {
        this(linesRemoved, newMatrix, 0);
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return ArrayOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }

}