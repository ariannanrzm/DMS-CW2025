package com.comp2042.game.logic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

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

    public void onLinesCleared(int lines) {
        linesClearedInCurrentLevel += lines;

        if (linesClearedInCurrentLevel >= LINES_PER_LEVEL) {
            linesClearedInCurrentLevel -= LINES_PER_LEVEL;
            levelUp();
        }
    }

    private void levelUp() {
        if (currentLevel.get() < MAX_LEVEL) {
            currentLevel.set(currentLevel.get() + 1);
        }
    }

    public double getCurrentSpeed() {
        int index = currentLevel.get() - 1;
        if (index < 0) index = 0;
        if (index >= SPEED_CURVE.length) index = SPEED_CURVE.length - 1;
        return SPEED_CURVE[index];
    }

    public IntegerProperty levelProperty() {
        return currentLevel;
    }

    public int getCurrentLevel() {
        return currentLevel.get();
    }

    public void reset() {
        currentLevel.set(1);
        linesClearedInCurrentLevel = 0;
    }
}