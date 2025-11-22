package com.comp2042;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for performing matrix-related operations used by the Tetris board.
 * Responsibilities include:
 *  - collision detection
 *  - out-of-bounds checks
 *  - merging bricks into the board
 *  - clearing completed rows (without score logic)
 *  - matrix copying utilities
 */
public final class MatrixOperations {

    // Prevent instantiation
    private MatrixOperations() {}

    /**
     * Checks whether placing a brick at (x, y) would collide with existing blocks
     * or exceed board boundaries.
     *
     * @param matrix board matrix
     * @param brick  brick shape matrix
     * @param x      column offset
     * @param y      row offset
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int row = 0; row < brick.length; row++) {
            for (int col = 0; col < brick[row].length; col++) {

                if (brick[row][col] == 0)
                    continue; // skip empty cells

                int targetX = x + col; // column
                int targetY = y + row; // row

                if (isOutOfBounds(matrix, targetX, targetY)
                        || matrix[targetY][targetX] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns true if (targetX, targetY) is outside board bounds. */
    private static boolean isOutOfBounds(int[][] matrix, int targetX, int targetY) {
        return targetX < 0
                || targetY < 0
                || targetY >= matrix.length
                || targetX >= matrix[0].length;
    }

    /** Deep-copies a 2D array. */
    public static int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new int[original[i].length];
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    /**
     * Merges a brick into the board.
     *
     * @param board existing board
     * @param brick brick matrix
     * @param x     column offset
     * @param y     row offset
     */
    public static int[][] merge(int[][] board, int[][] brick, int x, int y) {
        int[][] updated = copy(board);

        for (int row = 0; row < brick.length; row++) {
            for (int col = 0; col < brick[row].length; col++) {
                if (brick[row][col] != 0) {
                    updated[y + row][x + col] = brick[row][col];
                }
            }
        }
        return updated;
    }

    /**
     * Removes all completed rows and returns a ClearRow object with:
     *  - number of cleared rows
     *  - updated board matrix
     */
    public static ClearRow removeCompletedRows(final int[][] matrix) {

        int height = matrix.length;
        int width = matrix[0].length;

        List<int[]> remaining = new ArrayList<>();
        int cleared = 0;

        // Collect rows that are NOT full
        for (int[] row : matrix) {
            if (isRowFilled(row)) {
                cleared++;
            } else {
                remaining.add(row.clone());
            }
        }

        // Build new matrix: empty rows on top, remaining rows below
        int[][] newMatrix = new int[height][width];

        int insertIndex = 0;

        // Insert empty rows first
        for (int i = 0; i < cleared; i++) {
            newMatrix[insertIndex++] = new int[width]; // all zeros
        }

        // Then insert remaining rows
        for (int[] row : remaining) {
            newMatrix[insertIndex++] = row;
        }

        return new ClearRow(cleared, newMatrix);
    }

    /** Returns true if a row is fully filled (no zeros). */
    private static boolean isRowFilled(int[] row) {
        for (int cell : row) {
            if (cell == 0) return false;
        }
        return true;
    }

    /** Deep copy of a list of 2D matrices. */
    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream()
                .map(MatrixOperations::copy)
                .collect(Collectors.toList());
    }
}
