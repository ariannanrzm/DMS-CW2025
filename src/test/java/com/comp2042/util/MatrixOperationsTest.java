package com.comp2042.util;

import com.comp2042.game.board.ClearRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrixOperationsTest {

    @Test
    void testIntersectDetected() {
        // Create a 5x5 board with a block in the middle
        int[][] board = new int[5][5];
        board[2][2] = 1;

        // Create a single block "brick"
        int[][] brick = {{1}};

        // Try to place brick at the block position (should collide)
        boolean collision = MatrixOperations.intersect(board, brick, 2, 2);
        assertTrue(collision, "Should detect collision at (2,2)");
    }

    @Test
    void testNoIntersectOnEmptySpace() {
        int[][] board = new int[5][5];
        board[2][2] = 1;
        int[][] brick = {{1}};

        // Try to place brick next the block (should not collide)
        boolean collision = MatrixOperations.intersect(board, brick, 2, 3);
        assertFalse(collision, "Should NOT detect collision at (2,3)");
    }

    @Test
    void testRemoveCompletedRows() {
        // Setup a board where the bottom row is FULL
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0},
                {1, 1, 1} // Full row
        };

        ClearRow result = MatrixOperations.removeCompletedRows(board);

        assertEquals(1, result.getLinesRemoved(), "Should remove 1 line");

        // The bottom row should now be empty (new row inserted at top)
        assertEquals(0, result.getNewMatrix()[2][0], "Bottom row should be cleared");
    }
}