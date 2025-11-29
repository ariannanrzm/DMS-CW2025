package com.comp2042.ui;

import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.MoveEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

/**
 * Handles all keyboard input and forwards actions to the GameController.
 */
public class InputHandler {

    private final GameController controller;

    public InputHandler(GameController controller) {
        this.controller = controller;
    }

    /** Attach key listeners to any Node */
    public void attachTo(Node node) {
        node.setFocusTraversable(true);
        node.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT, A  -> controller.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
            case RIGHT, D -> controller.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
            case UP, W    -> controller.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
            case DOWN, S  -> controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
            case SPACE    -> controller.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
            case C        -> controller.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)); // New Mapping
            case N, R       -> controller.createNewGame();
            case ESCAPE   -> controller.togglePause();
            default       -> { }
        }
        event.consume();
    }
}