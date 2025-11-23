package com.comp2042.game.board;

public final class DownData {

    private final ClearRow clearRow;
    private final ViewData viewData;
    private final int[][] boardMatrix;
    private final boolean gameOver;

    public DownData(ClearRow clearRow,
                    ViewData viewData,
                    int[][] boardMatrix,
                    boolean gameOver) {
        this.clearRow = clearRow;
        this.viewData = viewData;
        this.boardMatrix = boardMatrix;
        this.gameOver = gameOver;
    }

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }

    public int[][] getBoardMatrix() {
        return boardMatrix;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
