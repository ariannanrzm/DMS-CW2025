package com.comp2042.util;

import com.comp2042.game.board.ClearRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing static methods for 2D array manipulations.
 * Handles collision detection, matrix merging, and row removal logic.
 */
public final class MatrixOperations {

    // Prevent instantiation
    private MatrixOperations() {
    }

    /**
     * Checks whether placing a brick at (x, y) would collide with existing blocks
     * or exceed board boundaries.
     *
     * @param matrix board matrix
     * @param brick  brick shape matrix
     * @param x      column offset
     * @param y      row offset
     * @return true if a collision is detected, false otherwise.
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

    /**
     * Returns true if (targetX, targetY) is outside board bounds.
     */
    private static boolean isOutOfBounds(int[][] matrix, int targetX, int targetY) {
        return targetX < 0
                || targetY < 0
                || targetY >= matrix.length
                || targetX >= matrix[0].length;
    }


    /**
     * Merges a brick into the board.
     * Uses ArrayOperations to create a safe copy before modifying.
     */
    public static int[][] merge(int[][] board, int[][] brick, int x, int y) {
        int[][] updated = ArrayOperations.copy(board);

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
     * - number of cleared rows
     * - updated board matrix
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

    /**
     * Returns true if a row is fully filled (no zeros).
     */
    private static boolean isRowFilled(int[] row) {
        for (int cell : row) {
            if (cell == 0) return false;
        }
        return true;
    }
}

