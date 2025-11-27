
package com.comp2042.game.states;

import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.MoveEvent;

public class GameOverState implements GameState {

    public GameOverState(GameController context) { }

    @Override
    public DownData handleDownEvent(MoveEvent event) { return null; }

    @Override
    public ViewData handleLeftEvent(MoveEvent event) { return null; }

    @Override
    public ViewData handleRightEvent(MoveEvent event) { return null; }

    @Override
    public ViewData handleRotateEvent(MoveEvent event) { return null; }

    @Override
    public DownData handleHardDropEvent(MoveEvent event) { return null; }
}