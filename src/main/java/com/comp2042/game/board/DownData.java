package com.comp2042.game.board;

/**
 * It holds the information regarding cleared rows, the updated board state, the active brick's view data,
 * and whether the game has ended.
 */
public final class DownData {

    private final ClearRow clearRow;
    private final ViewData viewData;
    private final int[][] boardMatrix;
    private final boolean gameOver;

    /**
     * Constructs a new DownData instance with the specified results of a move operation.
     *
     * @param clearRow    The object containing details about cleared lines and score bonuses.
     * @param viewData    The view data representing the state of the active brick.
     * @param boardMatrix The updated 2D grid of the game board.
     * @param gameOver    True if the move resulted in a game-over condition, false otherwise.
     */
    public DownData(ClearRow clearRow,
                    ViewData viewData,
                    int[][] boardMatrix,
                    boolean gameOver) {
        this.clearRow = clearRow;
        this.viewData = viewData;
        this.boardMatrix = boardMatrix;
        this.gameOver = gameOver;
    }

    /**
     * Retrieves the information regarding any rows cleared during the move.
     *
     * @return The ClearRow object containing the count of cleared lines and score info.
     */
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
