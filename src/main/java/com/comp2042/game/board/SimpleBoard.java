package com.comp2042.game.board;

import com.comp2042.game.bricks.*;
import com.comp2042.game.scoring.Score;
import com.comp2042.util.MatrixOperations;


/**
 * The SimpleBoard class manages brick movement, rotation, spawning,
 * background merging, and row clearing.
 */

public class SimpleBoard implements Board {

    private static final int SPAWN_X = 4;
    private static final int SPAWN_Y = 0;

    private static final int MOVE_LEFT = -1;
    private static final int MOVE_RIGHT = 1;
    private static final int MOVE_DOWN = 1;

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private GamePoint currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height,  BrickGenerator brickGenerator) {
        this.width = width;
        this.height = height;
        this.brickGenerator = brickGenerator;
        this.currentGameMatrix = new int[width][height];
        this.brickRotator = new BrickRotator();
        this.score = new Score();
    }

    private boolean isMoveValid(int x, int y, int[][] brickMatrix) {
        return !MatrixOperations.intersect(
                currentGameMatrix,
                brickMatrix,
                x,
                y
        );
    }

    /**
     * Attempts to move the active brick by a given offset.
     *
     * @param dx horizontal movement
     * @param dy vertical movement
     * @return true if movement is successful, false if blocked by collision
     */

    private boolean tryMove(int dx, int dy) {

        GamePoint newOffset = currentOffset.translate(dx, dy);

        if (!isMoveValid((newOffset.x()), newOffset.y(), brickRotator.getCurrentShape())){
            return false;
        }

        currentOffset = newOffset;
        return true;
    }

    @Override
    public boolean moveBrickDown() {
        return tryMove(0, MOVE_DOWN);
    }

    @Override
    public boolean moveBrickLeft() {
        return tryMove(MOVE_LEFT, 0);
    }

    @Override
    public boolean moveBrickRight() {
        return tryMove(MOVE_RIGHT, 0);
    }

    @Override
    public boolean rotateLeftBrick() {
        GamePoint newPosition = brickRotator.tryRotate(currentGameMatrix, currentOffset);

        if (newPosition != null ) {
            currentOffset = newPosition;
            return true;
        }
        return false;
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);

        currentOffset = new GamePoint(SPAWN_X, SPAWN_Y);

        return !isMoveValid(
                currentOffset.x(),
                currentOffset.y(),
                brickRotator.getCurrentShape()
        );
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(
                brickRotator.getCurrentShape(),
                currentOffset.x(),
                currentOffset.y(),
                brickGenerator.getNextBrick().getShapeMatrix().get(0)
        );
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                currentOffset.x(),
                currentOffset.y()
        );
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.removeCompletedRows(currentGameMatrix);

        if (clearRow.getLinesRemoved() > 0) {
            score.addLinesCleared(clearRow.getLinesRemoved());
        }

        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }


    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public DownData hardDropBrick() {
        int rowsDropped = 0;

        // Move the brick down until collision
        while (tryMove(0, MOVE_DOWN)) {
            rowsDropped++;
        }

        score.addHardDrop(rowsDropped);

        mergeBrickToBackground();

        ClearRow clearRow = clearRows();

        // Create a new brick and check for game over
        boolean gameOver = createNewBrick();

        return new DownData(
                clearRow,
                getViewData(),
                getBoardMatrix(),
                gameOver
        );
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}
