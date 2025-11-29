package com.comp2042.game.scoring;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {
    private static final int SOFT_DROP_SCORE = 1;
    private static final int HARD_DROP_SCORE = 2;
    private static final int SCORE_SINGLE = 100;
    private static final int SCORE_DOUBLE = 300;
    private static final int SCORE_TRIPLE = 500;
    private static final int SCORE_TETRIS = 800;
    private static final int SCORE_COMBO_BONUS = 50;

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty lines = new SimpleIntegerProperty(0);

    private int comboCount = -1;

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty linesProperty() {
        return lines;
    }
    /**
     * Calculates and adds points for cleared lines.
     */

    public int addLinesCleared(int count) {
        if (count > 0) {
            comboCount++;

            int baseScore = 0;
            switch (count) {
                case 1 -> baseScore = SCORE_SINGLE;
                case 2 -> baseScore = SCORE_DOUBLE;
                case 3 -> baseScore = SCORE_TRIPLE;
                case 4 -> baseScore = SCORE_TETRIS;
            }

            int comboBonus = (comboCount > 0) ? (comboCount * SCORE_COMBO_BONUS) : 0;

            int totalPoints = baseScore + comboBonus;

            score.setValue(score.getValue() + totalPoints);
            lines.setValue(lines.getValue() + count);

            return totalPoints;
        }
        return 0;
    }

    /**
     * Resets the combo counter when a piece locks without clearing lines.
     */
    public void resetCombo() {
        comboCount = -1;
    }

    public int getComboCount() {
        return Math.max(0, comboCount);
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
        lines.setValue(0);
        comboCount = -1;
    }

    /** Convenience getter  */
    public int getScore() {
        return score.get();
    }
}
