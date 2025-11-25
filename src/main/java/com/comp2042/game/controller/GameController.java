package com.comp2042.game.controller;

import com.comp2042.ui.GuiController;
import com.comp2042.game.board.*;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.game.config.GameConfig;

/**
 * GameController handles only game logic and communication with the Board.
 */
public class GameController implements InputEventListener {

    /** Game logic board */
    private final Board board = new SimpleBoard(
            GameConfig.get().getBoardHeight(),
            GameConfig.get().getBoardWidth()
    );

    /** GUI controller (only to register event listeners; no UI calls here) */
    private final GuiController viewGuiController;

    public GameController(GuiController viewGuiController) {
        this.viewGuiController = viewGuiController;

        this.viewGuiController.setGameController(this);
        this.viewGuiController.setEventListener(this);

        board.createNewBrick();
        this.viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        this.viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();

        if (!canMove) {

            board.mergeBrickToBackground();
            ClearRow clearRow = handleLanding();
            boolean gameOver = board.createNewBrick();

            // refresh full board on landing
            DownData data = new DownData(clearRow,
                    board.getViewData(),
                    board.getBoardMatrix(),
                    gameOver);

            viewGuiController.refreshGameBackground(data.getBoardMatrix());
            viewGuiController.refreshBrick(board.getViewData());
            return data;
        }

        incrementSoftDropScore(event);

        if (event.getEventSource() == EventSource.USER) {
            board.getScore().addSoftDrop();
            viewGuiController.resetTimeline();
        }

        // refresh brick on move
        viewGuiController.refreshBrick(board.getViewData());

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
            board.getScore().addSoftDrop();
        }
    }


    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        viewGuiController.refreshBrick(board.getViewData());
        return null;
    }


    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        viewGuiController.refreshBrick(board.getViewData());
        return null;
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        viewGuiController.refreshBrick(board.getViewData());
        return null;
    }

    @Override
    public void createNewGame() {
        board.newGame();
    }

    /** Board getter kept for testing */
    public Board getBoard() {
        return board;
    }


}
