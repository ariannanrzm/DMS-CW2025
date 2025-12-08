package com.comp2042.game.states;

import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.events.MoveEvent;

/**
 * Interface for the State design pattern.
 * Defines how the game responds to input events (Move, Rotate, Drop) in different contexts
 * (e.g., while Playing, Paused, or Game Over).
 */
public interface GameState {
    DownData handleDownEvent(MoveEvent event);

    ViewData handleLeftEvent(MoveEvent event);

    ViewData handleRightEvent(MoveEvent event);

    ViewData handleRotateEvent(MoveEvent event);

    DownData handleHardDropEvent(MoveEvent event);

    ViewData handleHoldEvent(MoveEvent event);
}