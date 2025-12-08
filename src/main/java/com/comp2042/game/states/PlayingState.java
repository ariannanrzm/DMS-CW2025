
package com.comp2042.game.states;

import com.comp2042.game.board.ClearRow;
import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.MoveEvent;

/**
 * Represents the active state of the game where gameplay mechanics are live.
 * Handles inputs by moving bricks and checking for line clears or game-over conditions.
 */
public class PlayingState implements GameState {

    private final GameController context;

    public PlayingState(GameController context) {
        this.context = context;
    }

    /**
     * Handles the logic for moving a brick down.
     * If the brick cannot move, it locks the brick, clears rows, scores points,
     * and spawns a new brick.
     *
     * @param event The move event context.
     * @return DownData containing the updated board and game status.
     */

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

    /**
     * Handles the request to move the active brick to the left.
     * Updates the view to reflect the new position if the move is successful.
     *
     * @param event The move event.
     * @return null (ViewData is updated directly via the controller).
     */
    @Override
    public ViewData handleLeftEvent(MoveEvent event) {
        context.getBoard().moveBrickLeft();
        context.getGuiController().refreshBrick(context.getBoard().getViewData());
        return null;
    }

    /**
     * Handles the request to move the active brick to the right.
     * Updates the view to reflect the new position if the move is successful.
     *
     * @param event The move event.
     * @return null.
     */
    @Override
    public ViewData handleRightEvent(MoveEvent event) {
        context.getBoard().moveBrickRight();
        context.getGuiController().refreshBrick(context.getBoard().getViewData());
        return null;
    }

    /**
     * Handles the request to rotate the active brick.
     * Updates the view to reflect the new orientation if the rotation is valid.
     *
     * @param event The move event.
     * @return null.
     */
    @Override
    public ViewData handleRotateEvent(MoveEvent event) {
        context.getBoard().rotateLeftBrick();
        context.getGuiController().refreshBrick(context.getBoard().getViewData());
        return null;
    }

    /**
     * Handles the hard drop event, instantly locking the brick at the bottom.
     * Updates the board, scores points, checks for line clears, and transitions to Game Over if necessary.
     *
     * @param event The move event.
     * @return DownData containing the final state after the drop.
     */
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

    /**
     * Handles the request to swap the current brick with the held brick.
     * Updates the view to show the new active brick if the swap is allowed.
     *
     * @param event The move event.
     * @return The updated view data.
     */
    @Override
    public ViewData handleHoldEvent(MoveEvent event) {
        boolean success = context.getBoard().holdBrick();
        if (success) {
            context.getGuiController().refreshBrick(context.getBoard().getViewData());
        }
        return context.getBoard().getViewData();
    }
}