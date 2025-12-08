package com.comp2042.game.board;

import com.comp2042.util.ArrayOperations;

/**
 * An immutable data class that holds the result of a row-clearing operation.
 * It stores the number of lines removed, the updated board matrix, and the
 * calculated score bonus associated with the clear.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Constructs a new ClearRow object with full details including the score.
     *
     * @param linesRemoved The number of completed rows removed from the board.
     * @param newMatrix    The updated state of the board matrix after rows are removed.
     * @param scoreBonus   The score points calculated for this specific clear action.
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = ArrayOperations.copy(newMatrix);
        this.scoreBonus = scoreBonus;
    }

    /**
     * Compatibility constructor that defaults the score bonus to 0.
     * Useful for operations (like internal board updates or tests) where scoring is not required.
     *
     * @param linesRemoved The number of completed rows removed.
     * @param newMatrix    The updated state of the board matrix.
     */
    public ClearRow(int linesRemoved, int[][] newMatrix) {
        this(linesRemoved, newMatrix, 0);
    }

    /**
     * Retrieves the count of lines that were cleared.
     *
     * @return The number of lines removed (0 to 4).
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Retrieves the updated board matrix.
     * Returns a defensive copy to prevent external modification of the stored state.
     *
     * @return A 2D integer array representing the new board layout.
     */
    public int[][] getNewMatrix() {
        return ArrayOperations.copy(newMatrix);
    }

    /**
     * Retrieves the score points awarded for this clear.
     *
     * @return The calculated score bonus.
     */
    public int getScoreBonus() {
        return scoreBonus;
    }

}