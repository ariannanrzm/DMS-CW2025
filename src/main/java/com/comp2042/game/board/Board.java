package com.comp2042.game.board;

import com.comp2042.game.scoring.Score;

/**
 * Interface defining the core logic and operations of the Tetris game board.
 * It establishes the contract for game mechanics such as moving bricks, handling collisions,
 * managing the grid state, and tracking the score.
 */
public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    boolean holdBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    DownData hardDropBrick();

    void addGarbageRow();
}
