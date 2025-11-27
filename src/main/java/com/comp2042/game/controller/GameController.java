package com.comp2042.game.controller;

import com.comp2042.game.bricks.RandomBrickGenerator;
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
    private final Board board;

    /** GUI controller (only for view updates, not game logic) */
    private final GuiController viewGuiController;

    public GameController(GuiController viewGuiController) {
        this.viewGuiController = viewGuiController;

        this.board = new SimpleBoard(
                GameConfig.get().getBoardHeight(),
                GameConfig.get().getBoardWidth(),
                new RandomBrickGenerator()
        );

        this.viewGuiController.setGameController(this);
        this.viewGuiController.setEventListener(this);

        board.createNewBrick();
        this.viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        this.viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        if(viewGuiController.isGameOver()) return null;
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
        return board.clearRows();
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        if (viewGuiController.isGameOver()) return null;

        DownData data = board.hardDropBrick();

        // Full refresh and game over check.
        viewGuiController.refreshGameBackground(data.getBoardMatrix());
        viewGuiController.refreshBrick(data.getViewData());

        viewGuiController.showNotificationIfRowsCleared(data.getClearRow());

        if (data.isGameOver()) {
            viewGuiController.gameOver();
        }

        return data;
    }

    private void incrementSoftDropScore(MoveEvent event) {
        if (event.getEventSource() == EventSource.USER) {
            board.getScore().addSoftDrop();
        }
    }


    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (viewGuiController.isGameOver()) return null;
        board.moveBrickLeft();
        viewGuiController.refreshBrick(board.getViewData());
        return null;
    }


    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (viewGuiController.isGameOver()) return null;
        board.moveBrickRight();
        viewGuiController.refreshBrick(board.getViewData());
        return null;
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (viewGuiController.isGameOver()) return null;
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
