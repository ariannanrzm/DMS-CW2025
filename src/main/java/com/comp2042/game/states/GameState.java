package com.comp2042.game.states;

import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.events.MoveEvent;

public interface GameState {
    DownData handleDownEvent(MoveEvent event);

    ViewData handleLeftEvent(MoveEvent event);

    ViewData handleRightEvent(MoveEvent event);

    ViewData handleRotateEvent(MoveEvent event);

    DownData handleHardDropEvent(MoveEvent event);
}