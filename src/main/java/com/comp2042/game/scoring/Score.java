package com.comp2042.game.scoring;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {
    private static final int SOFT_DROP_SCORE = 1;
    private static final int HARD_DROP_SCORE = 2;

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Calculates and adds points for cleared lines.
     */

    public int addLinesCleared(int lines) {
        if (lines > 0) {
            int bonus = 50 * lines * lines;
            score.setValue(score.getValue() + bonus);
            return bonus;
        }
        return 0;
    }

    public void addSoftDrop() {
        score.setValue(score.getValue() + SOFT_DROP_SCORE);
    }

    /**
     * Adds score for hard drop based on the number of rows dropped.
     */
    public void addHardDrop(int rows) {
        score.setValue(score.getValue() + (rows * HARD_DROP_SCORE));
    }

    public void reset() {
        score.setValue(0);
    }

    /** Convenience getter  */
    public int getScore() {
        return score.get();
    }
}
