package com.comp2042.game.logic;

import java.util.prefs.Preferences;

public class HighScoreManager {

    private static final String KEY_HIGH_SCORE = "high_score_adventure";
    private static final String KEY_FASTEST_TIME = "fastest_time_adventure";

    private static final Preferences prefs = Preferences.userNodeForPackage(HighScoreManager.class);


    public static int getHighScore() {
        return prefs.getInt(KEY_HIGH_SCORE, 0);
    }

    public static boolean tryUpdateHighScore(int newScore) {
        int currentHigh = getHighScore();
        if (newScore > currentHigh) {
            prefs.putInt(KEY_HIGH_SCORE, newScore);
            return true; // New Record
        }
        return false;
    }


    public static int getFastestTime() {
        return prefs.getInt(KEY_FASTEST_TIME, Integer.MAX_VALUE);
    }

    public static boolean tryUpdateFastestTime(int seconds) {
        int currentFastest = getFastestTime();
        if (seconds < currentFastest) {
            prefs.putInt(KEY_FASTEST_TIME, seconds);
            return true; // New Record
        }
        return false;
    }
}