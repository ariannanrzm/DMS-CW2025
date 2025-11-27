package com.comp2042.game.states;

import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.MoveEvent;

public class PausedState implements GameState {

    private final GameController context;

    public PausedState(GameController context) {
        this.context = context;
    }

    @Override
    public DownData handleDownEvent(MoveEvent event) {
        // Return current state without changes so the UI doesn't crash or update
        return new DownData(null,
                context.getBoard().getViewData(),
                context.getBoard().getBoardMatrix(),
                false);
    }

    @Override
    public ViewData handleLeftEvent(MoveEvent event) { return null; }

    @Override
    public ViewData handleRightEvent(MoveEvent event) { return null; }

    @Override
    public ViewData handleRotateEvent(MoveEvent event) { return null; }

    @Override
    public DownData handleHardDropEvent(MoveEvent event) { return null; }
}