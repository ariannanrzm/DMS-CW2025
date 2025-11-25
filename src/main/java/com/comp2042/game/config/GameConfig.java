package com.comp2042.game.config;

/**
 * Singleton Configuration Class.
 * Centralizes all game constants (dimensions, sizing, timings).
 */
public class GameConfig {

    private static final GameConfig INSTANCE = new GameConfig();
    private static final int BRICK_SIZE = 20;
    private static final int TOP_OFFSET = -45;
    private static final int ARC_RADIUS = 9;
    private static final int DROP_INTERVAL_MS = 400;
    private static final int HIDDEN_ROWS = 2;
    private static final int BOARD_ROWS = 25;
    private static final int BOARD_COLUMNS = 10;

    private GameConfig() {}

    // Global Access Point
    public static GameConfig get() {
        return INSTANCE;
    }

    // Getters
    public int getBoardColumns() { return BOARD_COLUMNS; }
    public int getBoardRows() { return BOARD_ROWS; }
    public int getBrickSize() { return BRICK_SIZE; }
    public int getHiddenRows() { return HIDDEN_ROWS; }
    public double getArcRadius() { return ARC_RADIUS; }
    public double getDropInterval() { return DROP_INTERVAL_MS; }
    public int getTopOffset() { return TOP_OFFSET; }
}