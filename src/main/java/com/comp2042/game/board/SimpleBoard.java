package com.comp2042.game.board;

import com.comp2042.game.bricks.*;
import com.comp2042.game.scoring.Score;
import com.comp2042.util.MatrixOperations;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * The SimpleBoard class manages brick movement, rotation, spawning,
 * background merging, and row clearing.
 */

public class SimpleBoard implements Board {

    private static final int SPAWN_X = 3;
    private static final int SPAWN_Y = 0;
    private static final int MOVE_LEFT = -1;
    private static final int MOVE_RIGHT = 1;
    private static final int MOVE_DOWN = 1;
    private static final int NEXT_BRICK_COUNT = 3;

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private GamePoint currentOffset;
    private final Score score;
    private Brick activeBrick;
    private Brick heldBrick;
    private boolean canHold = true;

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

    // Calculate where the ghost lands
    private int calculateGhostY() {
        int ghostY = currentOffset.y();
        int[][] currentShape = brickRotator.getCurrentShape();

        while (true) {
            if (isMoveValid(currentOffset.x(), ghostY + 1, currentShape)) {
                ghostY++;
            } else {
                break;
            }
        }

        return ghostY;
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
        activeBrick = brickGenerator.getBrick();
        brickRotator.setBrick(activeBrick);
        currentOffset = new GamePoint(SPAWN_X, SPAWN_Y);
        canHold = true;

        return !isMoveValid(
                currentOffset.x(),
                currentOffset.y(),
                brickRotator.getCurrentShape()
        );
    }

    @Override
    public boolean holdBrick() {
        if (!canHold) return false;

        Brick brickToHold = activeBrick;

        if (heldBrick == null) {
            heldBrick = brickToHold;
            createNewBrick();
        } else {
            // Swap current with held
            Brick temp = heldBrick;
            heldBrick = brickToHold;
            activeBrick = temp;

            brickRotator.setBrick(activeBrick);
            currentOffset = new GamePoint(SPAWN_X, SPAWN_Y);
        }

        canHold = false; // Prevent holding again until piece locks
        return true;
    }


    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }



    @Override
    public ViewData getViewData() {
        List<int[][]> nextBricks = brickGenerator.getNextBricks(NEXT_BRICK_COUNT).stream()
                .map(b -> b.getShapeMatrix().get(0))
                .collect(Collectors.toList());

        int[][] heldBrickData = (heldBrick != null) ? heldBrick.getShapeMatrix().get(0) : null;

        return new ViewData(
                brickRotator.getCurrentShape(),
                currentOffset.x(),
                currentOffset.y(),
                calculateGhostY(),
                nextBricks,
                heldBrickData
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
        } else {
            score.resetCombo();
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
        heldBrick = null;
        createNewBrick();
    }

    @Override
    public void addGarbageRow() {
        // Shift all rows up by 1
        int rows = currentGameMatrix.length;
        int cols = currentGameMatrix[0].length;

        for (int i = 0; i < rows - 1; i++) {
            currentGameMatrix[i] = currentGameMatrix[i + 1];
        }

        // 2. Create the new garbage row
        int[] garbageRow = new int[cols];
        int gapIndex = ThreadLocalRandom.current().nextInt(cols);

        for (int j = 0; j < cols; j++) {
            if (j == gapIndex) {
                garbageRow[j] = 0; // The gap
            } else {
                garbageRow[j] = ThreadLocalRandom.current().nextInt(1, 8);
            }
        }
        currentGameMatrix[rows - 1] = garbageRow;
    }
}
