package com.comp2042;


/**
 * The GameController class handles all interactions between the GUI controller
 * and the underlying game logic (Board). It processes movement events,
 * coordinates updates to the board state, and manages transitions such as
 * landing, line clears, scoring, and spawning new bricks.
 */

public class GameController implements InputEventListener {

    private static final int BOARD_ROWS = 25;
    private static final int BOARD_COLUMNS = 10;
    private static final int SOFT_DROP_SCORE = 1;

    private Board board = new SimpleBoard(BOARD_ROWS, BOARD_COLUMNS);
    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    /**
     * Handles a downward movement event. This method delegates movement,
     * landing, scoring, row clearing, and brick spawning to smaller helper
     * methods for improved clarity.
     *
     * @param event the movement event triggered by the user or internal thread
     * @return DownData object containing row-clear information and updated view data
     */

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = handleBrickMovement();

        if (!canMove) {
            ClearRow clearRow = handleLanding();
            handleRowClear(clearRow);

            boolean gameOver = trySpawnNewBrick();
            if (gameOver) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            return new DownData(clearRow, board.getViewData());
        } else {
            incrementSoftDropScore(event);
            return new DownData(null, board.getViewData());
        }
    }

    /**
     * Attempts to move the active brick downward.
     *
     * @return true if the brick can move, false if it has landed
     */

    private boolean handleBrickMovement() {
        return board.moveBrickDown();
    }

    /**
     * Handles logic upon brick landing: merging the brick into the background
     * and clearing completed rows.
     *
     * @return a ClearRow object describing any rows cleared
     */

    private ClearRow handleLanding() {
        board.mergeBrickToBackground();
        return board.clearRows();
    }

    /**
     * Updates the score if row clearing has occurred.
     *
     * @param clearRow the result of the row-clear operation
     */

    private void handleRowClear(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }
    }

    /**
     * Attempts to create a new brick. If the new brick overlaps with existing tiles,
     * the game is considered over.
     *
     * @return true if the game should end, false otherwise
     */

    private boolean trySpawnNewBrick() {
        return board.createNewBrick();
    }

    /**
     * Increments the score only for user-initiated soft drops.
     *
     * @param event the downward movement event
     */

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
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    public Board getBoard() {
        return board;
    }

    public GuiController getGuiController() {
        return viewGuiController;
    }
}
