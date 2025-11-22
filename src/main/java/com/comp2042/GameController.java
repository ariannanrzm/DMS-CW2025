package com.comp2042;

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
    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            ClearRow clearRow = handleLanding();

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

    /** Attempt to move brick down; if landed, merge + clear rows. */
    private ClearRow handleLanding() {
        board.mergeBrickToBackground();
        return board.clearRows(); // scoring now handled inside SimpleBoard.clearRows()
    }

    /** Spawns next brick; returns true if game-over condition occurs. */
    private boolean trySpawnNewBrick() {
        return board.createNewBrick();
    }

    /** Award soft-drop points only for user input (not thread gravity). */
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
