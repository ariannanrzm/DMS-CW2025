package com.comp2042.game.logic;

import java.util.prefs.Preferences;

/**
 * Manages the persistence of high scores and fastest times using Java Preferences.
 * Allows the game to save and retrieve records across application restarts without external files.
 */
public class HighScoreManager {

    private static final String KEY_HIGH_SCORE = "high_score_adventure";
    private static final String KEY_FASTEST_TIME = "fastest_time_adventure";

    private static final Preferences prefs = Preferences.userNodeForPackage(HighScoreManager.class);

    /**
     * Retrieves the current high score recorded for Adventure Mode.
     *
     * @return The highest score achieved, or 0 if no record exists.
     */
    public static int getHighScore() {
        return prefs.getInt(KEY_HIGH_SCORE, 0);
    }

    /**
     * Compares a new score against the stored high score and updates it if the new score is higher.
     *
     * @param newScore The score to check.
     * @return true if a new high score was set, false otherwise.
     */
    public static boolean tryUpdateHighScore(int newScore) {
        int currentHigh = getHighScore();
        if (newScore > currentHigh) {
            prefs.putInt(KEY_HIGH_SCORE, newScore);
            return true; // New Record
        }
        return false;
    }

    /**
     * Retrieves the fastest game completion time recorded for Adventure Mode.
     *
     * @return The fastest time in seconds, or {@code Integer.MAX_VALUE} if no record exists.
     */
    public static int getFastestTime() {
        return prefs.getInt(KEY_FASTEST_TIME, Integer.MAX_VALUE);
    }

    /**
     * Compares a new completion time against the stored fastest time and updates it if the new time is lower (faster).
     *
     * @param seconds The time in seconds to check.
     * @return true if a new fastest time record was set, false otherwise.
     */
    public static boolean tryUpdateFastestTime(int seconds) {
        int currentFastest = getFastestTime();
        if (seconds < currentFastest) {
            prefs.putInt(KEY_FASTEST_TIME, seconds);
            return true; // New Record
        }
        return false;
    }
}