package com.comp2042.game.board;

import com.comp2042.util.ArrayOperations;
import java.util.List;

/**
 * ViewData is an immutable data transfer object used to pass
 * rendering information from the game logic (Board) to the UI.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int ghostYPosition;
    private final List<int[][]> nextBricks;
    private final int[][] heldBrickData;

    /**
     * Constructs a new immutable ViewData object.
     *
     * @param brickData       the matrix of the active brick
     * @param xPosition       x-position of the active brick
     * @param yPosition       y-position of the active brick
     * @param ghostYPosition  y-position of the ghost brick
     * @param nextBricks      list of matrices for upcoming bricks
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int ghostYPosition, List<int[][]> nextBricks, int[][] heldBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostYPosition = ghostYPosition;
        this.nextBricks = nextBricks;
        this.heldBrickData = heldBrickData;
    }

    /**
     * @return a defensive copy of the active brick's shape matrix
     */
    public int[][] getBrickData() {
        return ArrayOperations.copy(brickData);
    }

    /**
     * @return the x-coordinate of the active brick
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * @return the y-coordinate of the active brick
     */
    public int getyPosition() {
        return yPosition;
    }


    public int getGhostYPosition() {
        return ghostYPosition;
    }
    /**
     * @return a defensive copy of the next brick's shape matrix
     */
    public List<int[][]> getNextBricks() {
        return ArrayOperations.deepCopyList(nextBricks);
    }

    public int[][] getHeldBrickData() {
        return heldBrickData != null ? ArrayOperations.copy(heldBrickData) : null;
    }

}
