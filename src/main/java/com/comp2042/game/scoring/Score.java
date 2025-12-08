package com.comp2042.game.scoring;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import com.comp2042.game.config.GameConfig;

/**
 * Manages the scoring system using JavaFX properties for UI binding.
 * Calculates points based on lines cleared, level multipliers, and combo bonuses.
 */
public final class Score {

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
            // Use GameConfig getters
            switch (count) {
                case 1 -> baseScore = GameConfig.get().getScoreSingle();
                case 2 -> baseScore = GameConfig.get().getScoreDouble();
                case 3 -> baseScore = GameConfig.get().getScoreTriple();
                case 4 -> baseScore = GameConfig.get().getScoreTetris();
            }

            int comboBonus = (comboCount > 0) ? (comboCount * GameConfig.get().getScoreComboBonus()) : 0;

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
        score.setValue(score.getValue() + GameConfig.get().getSoftDropScore());;
    }

    /**
     * Adds score for hard drop based on the number of rows dropped.
     */
    public void addHardDrop(int rows) {
        score.setValue(score.getValue() + (rows * GameConfig.get().getHardDropScore()));
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
