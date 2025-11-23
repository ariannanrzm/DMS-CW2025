package com.comp2042.game.controller;

import com.comp2042.ui.GuiController;
import com.comp2042.game.board.*;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;

/**
 * GameController handles only game logic and communication with the Board.
 * It no longer performs GUI updates or input handling (pure MVC separation).
 */
public class GameController implements InputEventListener {

    private static final int BOARD_ROWS = 25;
    private static final int BOARD_COLUMNS = 10;
    private static final int SOFT_DROP_SCORE = 1;

    /** Game logic board */
    private final Board board = new SimpleBoard(BOARD_ROWS, BOARD_COLUMNS);

    /** GUI controller (only to register event listeners; no UI calls here) */
    private final GuiController viewGuiController;

    public GameController(GuiController viewGuiController) {
        this.viewGuiController = viewGuiController;

        // Let GUI send events to this controller
        viewGuiController.setEventListener(this);

        // GameController no longer calls GUI methods such as initGameView() or bindScore()
        board.createNewBrick();
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            ClearRow clearRow = handleLanding();
            boolean gameOver = board.createNewBrick();

            return new DownData(clearRow,
                    board.getViewData(),
                    board.getBoardMatrix(),
                    gameOver);
        }

        incrementSoftDropScore(event);

        return new DownData(null,
                board.getViewData(),
                board.getBoardMatrix(),
                false);
    }

    private ClearRow handleLanding() {
        board.mergeBrickToBackground();
        return board.clearRows();
    }

    private void incrementSoftDropScore(MoveEvent event) {
        if (event.getEventSource() == EventSource.USER) {
            board.getScore().add(SOFT_DROP_SCORE);
        }
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
    }

    /** Board getter kept for testing */
    public Board getBoard() {
        return board;
    }

    // Removed getGuiController() according to MVC separation (Commit 8)
}
