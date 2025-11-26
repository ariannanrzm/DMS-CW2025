package com.comp2042.ui;

import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.MoveEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import javafx.scene.input.KeyEvent;

import java.security.Key;

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

    private void handleKeyPress(KeyEvent keyEvent) {

        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.LEFT || code == KeyCode.A) {
            controller.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
            controller.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
        } else if (code == KeyCode.UP || code == KeyCode.W) {
            controller.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        } else if (code == KeyCode.DOWN || code == KeyCode.S) {
            controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        } else if (code == KeyCode.SPACE) {
            controller.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
        } else if (code == KeyCode.N) {
            controller.createNewGame();
        }

        keyEvent.consume();
    }
}

