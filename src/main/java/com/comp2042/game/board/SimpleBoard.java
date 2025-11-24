package com.comp2042.game.board;

import com.comp2042.game.bricks.*;
import com.comp2042.game.scoring.Score;
import com.comp2042.util.ArrayOperations;
import com.comp2042.util.MatrixOperations;

import java.awt.*;

/**
 * The SimpleBoard class manages brick movement, rotation, spawning,
 * background merging, and row clearing.
 * Refactoring to reduce duplication and eliminate magic numbers.
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
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Attempts to move the active brick by a given offset.
     *
     * @param dx horizontal movement
     * @param dy vertical movement
     * @return true if movement is successful, false if blocked by collision
     */

    private boolean tryMove(int dx, int dy) {
        int[][] currentMatrix = ArrayOperations.copy(currentGameMatrix);

        Point newOffset = new Point(currentOffset);
        newOffset.translate(dx, dy);

        boolean conflict = MatrixOperations.intersect(
                currentMatrix,
                brickRotator.getCurrentShape(),
                (int) newOffset.getX(),
                (int) newOffset.getY()
        );

        if (conflict) {
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
        int[][] currentMatrix = ArrayOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(
                currentMatrix,
                nextShape.getShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );

        if (conflict) {
            return false;
        }

        brickRotator.applyRotation(nextShape.getPosition());
        return true;
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);

        currentOffset = new Point(SPAWN_X, SPAWN_Y);

        return MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
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
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                brickGenerator.getNextBrick().getShapeMatrix().get(0)
        );
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.removeCompletedRows(currentGameMatrix);

        if (clearRow.getLinesRemoved() > 0) {
            int bonus = calculateScoreBonus(clearRow.getLinesRemoved());
            score.add(bonus);
        }

        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    private int calculateScoreBonus(int lines) {
        return 50 * lines * lines;
    }



    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}
