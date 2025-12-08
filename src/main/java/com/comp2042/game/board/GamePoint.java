package com.comp2042.game.board;


/**
 * A pure data structure for coordinates.
 * Replaces java.awt.Point to remove AWT UI dependencies from the Model.
 */
public record GamePoint(int x, int y) {
    public GamePoint translate(int dx, int dy) {
      return new GamePoint(x + dx, y + dy);

    }
}
