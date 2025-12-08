package com.comp2042.game.logic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the difficulty progression of the game.
 * Tracks lines cleared to determine when to level up and adjusts the game speed accordingly.
 */
public class LevelManager {

    private static final int MAX_LEVEL = 7;
    private static final int LINES_PER_LEVEL = 5; // User has to complete 5 lines

    // Speed curve
    private static final double[] SPEED_CURVE = {
            900, // Level 1
            800, // Level 2
            700, // Level 3
            600, // Level 4
            500, // Level 5
            400 // Level 6 (Final)


    };

    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private int linesClearedInCurrentLevel = 0;

    /**
     * Updates progress based on lines cleared and triggers a level up if the threshold is reached.
     *
     * @param lines The number of lines cleared in the recent action.
     */
    public void onLinesCleared(int lines) {
        linesClearedInCurrentLevel += lines;

        if (linesClearedInCurrentLevel >= LINES_PER_LEVEL) {
            linesClearedInCurrentLevel -= LINES_PER_LEVEL;
            levelUp();
        }
    }

    /**
     * Increments the current level by one, provided the maximum level has not been reached.
     */
    private void levelUp() {
        if (currentLevel.get() < MAX_LEVEL) {
            currentLevel.set(currentLevel.get() + 1);
        }
    }

    /**
     * Retrieves the drop interval (game speed) corresponding to the current level.
     *
     * @return The delay in milliseconds between automatic brick drops.
     */
    public double getCurrentSpeed() {
        int index = currentLevel.get() - 1;
        if (index < 0) index = 0;
        if (index >= SPEED_CURVE.length) index = SPEED_CURVE.length - 1;
        return SPEED_CURVE[index];
    }

    /**
     * Retrieves the level property, allowing the UI to bind to and observe level changes.
     *
     * @return The IntegerProperty representing the current level.
     */
    public IntegerProperty levelProperty() {
        return currentLevel;
    }

    /**
     * Retrieves the current level as a primitive integer.
     *
     * @return The current level number.
     */
    public int getCurrentLevel() {
        return currentLevel.get();
    }

    /**
     * Resets the level manager to its initial state (Level 1 with 0 lines cleared).
     */
    public void reset() {
        currentLevel.set(1);
        linesClearedInCurrentLevel = 0;
    }
}