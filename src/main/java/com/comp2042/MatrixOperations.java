package com.comp2042;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing operations for matrix manipulation used throughout the Tetris game.
 * Includes collision detection, merging brick shapes into the board, row clearing,
 * deep copying, and general matrix utilities.
 *
 * Refactoring includes:
 * - introducing constants for magic numbers,
 * - extracting helper methods for clarity,
 * - adding documentation,
 * - improving readability while preserving original behaviour.
 */
public final class MatrixOperations {

    /** Score multiplier used for computing row clear bonus. */
    private static final int SCORE_MULTIPLIER = 50;

    // Prevent instantiation
    private MatrixOperations() {}

    /**
     * Checks whether a brick placed at coordinates (x, y) would intersect
     * with the existing filled cells of a board matrix.
     *
     * @param matrix the game board matrix
     * @param brick  the brick shape matrix
     * @param x      x-coordinate offset
     * @param y      y-coordinate offset
     * @return true if intersection or out-of-bounds occurs, false otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {

                // Note: indexing uses brick[j][i] based on original implementation
                int targetX = x + i;
                int targetY = y + j;

                boolean brickFilled = brick[j][i] != 0;
                if (brickFilled && (isOutOfBounds(matrix, targetX, targetY)
                        || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines whether the given coordinates fall outside the matrix bounds.
     *
     * @param matrix the matrix
     * @param targetX x-coordinate
     * @param targetY y-coordinate
     * @return true if coordinates are out of bounds, false otherwise
     */
    private static boolean isOutOfBounds(int[][] matrix, int targetX, int targetY) {
        return !(targetX >= 0
                && targetY < matrix.length
                && targetX < matrix[targetY].length);
    }

    /**
     * Creates a deep copy of a 2D matrix.
     *
     * @param original the original matrix
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
     * Merges a brick shape into the current board matrix at the given offset.
     *
     * @param board the current board matrix
     * @param brick the brick shape matrix
     * @param x     x-offset
     * @param y     y-offset
     * @return a new matrix with the brick merged in
     */
    public static int[][] merge(int[][] board, int[][] brick, int x, int y) {
        int[][] updated = copy(board);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {

                // Note: indexing brick[j][i] preserved for correctness
                if (brick[j][i] != 0) {
                    updated[y + j][x + i] = brick[j][i];
                }
            }
        }
        return updated;
    }

    /**
     * Clears all fully-filled rows in the board matrix and computes the score bonus.
     *
     * @param matrix the board matrix
     * @return ClearRow containing the updated matrix, number of rows removed, and score bonus
     */
    public static ClearRow removeCompletedRows(final int[][] matrix) {
        int[][] updatedMatrix = new int[matrix.length][matrix[0].length];
        Deque<int[]> remainingRows = new ArrayDeque<>();
        List<Integer> cleared = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] rowCopy = new int[matrix[i].length];
            boolean filled = isRowFilled(matrix[i]);

            System.arraycopy(matrix[i], 0, rowCopy, 0, matrix[i].length);

            if (filled) {
                cleared.add(i);
            } else {
                remainingRows.add(rowCopy);
            }
        }

        fillFromBottom(updatedMatrix, remainingRows);

        int scoreBonus = computeScoreBonus(cleared.size());
        return new ClearRow(cleared.size(), updatedMatrix, scoreBonus);
    }

    /** Checks if a row is fully filled (no zeros). */
    private static boolean isRowFilled(int[] row) {
        for (int cell : row) {
            if (cell == 0) return false;
        }
        return true;
    }

    /** Computes score bonus from the number of cleared rows. */
    private static int computeScoreBonus(int rowsCleared) {
        return SCORE_MULTIPLIER * rowsCleared * rowsCleared;
    }

    /** Fills matrix rows bottom-up using remaining rows. */
    private static void fillFromBottom(int[][] matrix, Deque<int[]> rows) {
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = rows.pollLast();
            if (row != null) {
                matrix[i] = row;
            } else {
                break;
            }
        }
    }

    /**
     * Creates a deep copy of a list of matrices.
     *
     * @param list list of 2D matrices
     * @return list where each matrix has been deep copied
     */
    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}
