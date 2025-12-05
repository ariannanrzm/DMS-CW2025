
package com.comp2042.game.states;

import com.comp2042.game.board.ClearRow;
import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.MoveEvent;

public class PlayingState implements GameState {

    private final GameController context;

    public PlayingState(GameController context) {
        this.context = context;
    }

    @Override
    public DownData handleDownEvent(MoveEvent event) {
        boolean canMove = context.getBoard().moveBrickDown();

        if (!canMove) {
            context.getBoard().mergeBrickToBackground();
            ClearRow clearRow = context.getBoard().clearRows();

            if (clearRow.getLinesRemoved() > 0) {
                context.notifyLinesCleared(clearRow.getLinesRemoved());
            }

            boolean gameOver = context.getBoard().createNewBrick();

            DownData data = new DownData(clearRow,
                    context.getBoard().getViewData(),
                    context.getBoard().getBoardMatrix(),
                    gameOver);

            // Update View
            context.getGuiController().refreshGameBackground(data.getBoardMatrix());
            context.getGuiController().refreshBrick(context.getBoard().getViewData());


            // Only refresh the brick if the game is not over
            if (!gameOver) {
                context.getGuiController().refreshBrick(context.getBoard().getViewData());
            }

            if (gameOver) {
                    context.setState(context.getGameOverState());
                    context.getGuiController().gameOver();
                }

            return data;
        }

        if (event.getEventSource() == EventSource.USER) {
            context.getBoard().getScore().addSoftDrop();
            context.getGuiController().resetTimeline();
        }

        context.getGuiController().refreshBrick(context.getBoard().getViewData());

        return new DownData(null,
                context.getBoard().getViewData(),
                context.getBoard().getBoardMatrix(),
                false);
    }

    @Override
    public ViewData handleLeftEvent(MoveEvent event) {
        context.getBoard().moveBrickLeft();
        context.getGuiController().refreshBrick(context.getBoard().getViewData());
        return null;
    }

    @Override
    public ViewData handleRightEvent(MoveEvent event) {
        context.getBoard().moveBrickRight();
        context.getGuiController().refreshBrick(context.getBoard().getViewData());
        return null;
    }

    @Override
    public ViewData handleRotateEvent(MoveEvent event) {
        context.getBoard().rotateLeftBrick();
        context.getGuiController().refreshBrick(context.getBoard().getViewData());
        return null;
    }

    @Override
    public DownData handleHardDropEvent(MoveEvent event) {
        DownData data = context.getBoard().hardDropBrick();

        context.getGuiController().refreshGameBackground(data.getBoardMatrix());
        context.getGuiController().refreshBrick(data.getViewData());
        context.getGuiController().showNotificationIfRowsCleared(data.getClearRow());

        if (data.getClearRow().getLinesRemoved() > 0) {
            context.notifyLinesCleared(data.getClearRow().getLinesRemoved());
        }

        // Only refresh the brick if the game is not over
        if (!data.isGameOver()) {
            context.getGuiController().refreshBrick(data.getViewData());
        }

        if (data.isGameOver()) {
            context.setState(context.getGameOverState());
            context.getGuiController().gameOver();
        }

        return data;
    }

    @Override
    public ViewData handleHoldEvent(MoveEvent event) {
        boolean success = context.getBoard().holdBrick();
        if (success) {
            context.getGuiController().refreshBrick(context.getBoard().getViewData());
        }
        return context.getBoard().getViewData();
    }
}