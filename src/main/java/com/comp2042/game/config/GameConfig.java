package com.comp2042.game.config;

/**
 * Singleton Configuration Class.
 * Centralizes all game constants (dimensions, sizing, timings).
 */
public class GameConfig {

    // Board & Visuals
    private static final GameConfig INSTANCE = new GameConfig();
    private static final int BRICK_SIZE = 20;
    private static final int TOP_OFFSET = -43;
    private static final int ARC_RADIUS = 9;
    private static final int DROP_INTERVAL_MS = 400;
    private static final int HIDDEN_ROWS = 2;
    private static final int BOARD_HEIGHT = 25;
    private static final int BOARD_WIDTH = 10;

    // Scoring
    private static final int SOFT_DROP_SCORE = 1;
    private static final int HARD_DROP_SCORE = 2;
    private static final int SCORE_SINGLE = 100;
    private static final int SCORE_DOUBLE = 300;
    private static final int SCORE_TRIPLE = 500;
    private static final int SCORE_TETRIS = 800;
    private static final int SCORE_COMBO_BONUS = 50;

    private GameConfig() {}

    // Global Access Point
    public static GameConfig get() {
        return INSTANCE;
    }

    // Getters
    public int getBoardWidth() { return BOARD_WIDTH; }
    public int getBoardHeight() { return BOARD_HEIGHT; }
    public int getBrickSize() { return BRICK_SIZE; }
    public int getHiddenRows() { return HIDDEN_ROWS; }
    public double getArcRadius() { return ARC_RADIUS; }
    public double getDropInterval() { return DROP_INTERVAL_MS; }
    public int getTopOffset() { return TOP_OFFSET; }

    // Scoring Getters
    public int getSoftDropScore() { return SOFT_DROP_SCORE; }
    public int getHardDropScore() { return HARD_DROP_SCORE; }
    public int getScoreSingle() { return SCORE_SINGLE; }
    public int getScoreDouble() { return SCORE_DOUBLE; }
    public int getScoreTriple() { return SCORE_TRIPLE; }
    public int getScoreTetris() { return SCORE_TETRIS; }
    public int getScoreComboBonus() { return SCORE_COMBO_BONUS; }
}