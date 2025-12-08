package com.comp2042.util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for generic array manipulations.
 * This class is independent of game logic and strictly handles data cloning.
 */
public class ArrayOperations {

    private ArrayOperations() {}

    /** Deep copies a 2D integer array. */
    public static int[][] copy(int[][] original){
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new int[original[i].length];
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    /** Deep copy of a list of 2D matrices. */
    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream()
                .map(ArrayOperations::copy)
                .collect(Collectors.toList());
    }
}
