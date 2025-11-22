package com.comp2042;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class containing static helper methods for manipulating 2D matrices
 * used by the Tetris board. Provides collision checks, merging operations,
 * row-clearing, copying, and deep-copy utilities.
 */
public final class MatrixOperations {

    /** Score multiplier applied when computing row-clear bonuses. */
    private static final int SCORE_MULTIPLIER = 50;

    // Prevent instantiation
    private MatrixOperations() {}

    /**
     * Checks whether placing a brick at (x, y) would collide with an existing block
     * or fall outside the board boundaries.
     *
     * @param matrix the game board matrix
     * @param brick  the brick shape matrix
     * @param x      horizontal offset
     * @param y      vertical offset
     * @return true if a collision or boundary error occurs
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {

                int targetX = x + i;
                int targetY = y + j;

                boolean brickFilled = brick[i][j] != 0;
                if (brickFilled && (isOutOfBounds(matrix, targetX, targetY)
                        || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the given coordinates lie outside the matrix.
     *
     * @param matrix the board matrix
     * @param targetX column index
     * @param targetY row index
     */
    private static boolean isOutOfBounds(int[][] matrix, int targetX, int targetY) {
        return targetX < 0
                || targetY < 0
                || targetY >= matrix.length
                || targetX >= matrix[0].length;
    }

    /**
     * Produces a deep copy of a 2D matrix.
     *
     * @param original the matrix to copy
     * @return a deep copy of the matrix
     */
    public static int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new int[original[i].length];
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    /**
     * Merges a brick's filled cells into the board matrix at the given offset.
     *
     * @param board the board matrix
     * @param brick the brick shape
     * @param x     x-offset
     * @param y     y-offset
     * @return a new matrix containing the merged result
     */
    public static int[][] merge(int[][] board, int[][] brick, int x, int y) {
        int[][] updated = copy(board);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                if (brick[i][j] != 0) {
                    updated[y + j][x + i] = brick[i][j];
                }
            }
        }
        return updated;
    }

    /**
     * Removes all completely filled rows from the board and returns the updated matrix
     * along with the number of cleared rows and score bonus.
     *
     * @param matrix the original board matrix
     * @return a ClearRow object containing updated board data
     */
    public static ClearRow removeCompletedRows(final int[][] matrix) {
        int[][] updatedMatrix = new int[matrix.length][matrix[0].length];
        Deque<int[]> remainingRows = new ArrayDeque<>();
        List<Integer> cleared = new ArrayList<>();

        for (int[] row : matrix) {
            int[] rowCopy = new int[row.length];
            System.arraycopy(row, 0, rowCopy, 0, row.length);

            if (isRowFilled(row)) {
                cleared.add(1);
            } else {
                remainingRows.add(rowCopy);
            }
        }

        fillFromBottom(updatedMatrix, remainingRows);

        int scoreBonus = computeScoreBonus(cleared.size());
        return new ClearRow(cleared.size(), updatedMatrix, scoreBonus);
    }

    /** Returns true if a row is entirely filled (contains no zeros). */
    private static boolean isRowFilled(int[] row) {
        for (int cell : row) {
            if (cell == 0) return false;
        }
        return true;
    }

    /** Computes the score bonus for the number of cleared rows. */
    private static int computeScoreBonus(int rowsCleared) {
        return SCORE_MULTIPLIER * rowsCleared * rowsCleared;
    }

    /** Writes rows into a matrix starting from the bottom. */
    private static void fillFromBottom(int[][] matrix, Deque<int[]> rows) {
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = rows.pollLast();
            if (row != null) {
                matrix[i] = row;
            } else break;
        }
    }

    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}
