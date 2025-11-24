package com.comp2042.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArrayOperationsTest {

    @Test
    void testCopyCreatesDeepCopy() {
        int[][] original = {
                {1, 2},
                {3, 4}
        };

        int[][] copy = ArrayOperations.copy(original);

        // Verify content is the same
        assertArrayEquals(original[0], copy[0]);
        assertArrayEquals(original[1], copy[1]);

        // Verify they are differeny objects (Deep Copy)
        original[0][0] = 99;
        assertEquals(1, copy[0][0], "Modifying original should not affect copy");
    }
}