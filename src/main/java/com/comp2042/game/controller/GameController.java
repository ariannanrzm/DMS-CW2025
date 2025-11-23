package com.comp2042.game.controller;

import com.comp2042.ui.GuiController;
import com.comp2042.game.board.*;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;

/**
 * The GameController class handles interactions between the GUI and the game logic (Board).
 * It processes movement events, manages brick updates, spawning, and game-over behaviour.
 * Scoring is now handled fully inside SimpleBoard.
 */
public class GameController implements InputEventListener {

    private static final int BOARD_ROWS = 25;
    private static final int BOARD_COLUMNS = 10;
    private static final int SOFT_DROP_SCORE = 1;

    private Board board = new SimpleBoard(BOARD_ROWS, BOARD_COLUMNS);

    /** The UI controller this game controller communicates with. */
    private final GuiController viewGuiController;

    public GameController(GuiController viewGuiController) {
        this.viewGuiController = viewGuiController;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = handleBrickMovement();

        if (!canMove) {
            ClearRow clearRow = handleLanding();
            handleRowClear(clearRow);

            boolean gameOver = trySpawnNewBrick();

            return new DownData(
                    clearRow,
                    board.getViewData(),
                    board.getBoardMatrix(),
                    gameOver
            );
        } else {
            incrementSoftDropScore(event);

            return new DownData(
                    null,
                    board.getViewData(),
                    board.getBoardMatrix(),
                    false
            );
        }
    }

    private ClearRow handleLanding() {
        board.mergeBrickToBackground();
        return board.clearRows();
    }

    private boolean trySpawnNewBrick() {
        return board.createNewBrick();
    }

    private void incrementSoftDropScore(MoveEvent event) {
        if (event.getEventSource() == EventSource.USER) {
            board.getScore().add(SOFT_DROP_SCORE);
        }
    }

    private boolean handleBrickMovement() {
        return board.moveBrickDown();
    }

    private void handleRowClear(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            // Score handled fully inside SimpleBoard
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

    public Board getBoard() {
        return board;
    }

    public GuiController getGuiController() {
        return viewGuiController;
    }
}
