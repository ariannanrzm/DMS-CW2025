package com.comp2042.game.events;

import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;

/**
 * Listener interface for handling game-related input events.
 * Defines the contract for processing user or system-triggered actions like moving, rotating, or dropping bricks.
 */
public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    DownData onHardDropEvent(MoveEvent event);

    ViewData onHoldEvent(MoveEvent event);

    void createNewGame();
}
