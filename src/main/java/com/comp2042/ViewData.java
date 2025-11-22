package com.comp2042;

/**
 * ViewData is an immutable data transfer object used to pass
 * rendering information from the game logic (Board) to the UI
 * (GuiController). It provides the current brick matrix, its
 * position on the board, and the next brick preview matrix.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;

    /**
     * Constructs a new immutable ViewData object.
     *
     * @param brickData       the matrix of the active brick
     * @param xPosition       x-position of the active brick
     * @param yPosition       y-position of the active brick
     * @param nextBrickData   matrix of the upcoming brick
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * @return a defensive copy of the active brick's shape matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
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

    /**
     * @return a defensive copy of the next brick's shape matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
