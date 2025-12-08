package com.comp2042.game.states;

import com.comp2042.MockGuiController;
import com.comp2042.game.bricks.Brick;
import com.comp2042.game.bricks.BrickGenerator;
import com.comp2042.game.bricks.OBrick;
import com.comp2042.game.config.GameMode;
import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.MoveEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatePatternTest {

    private GameController controller;
    private MockGuiController mockGui;

    // Defined the stub here to isolate the test from random generation.
    // Guarantees a simple O-Brick (Square) is always used.
    class StubBrickGenerator implements BrickGenerator {
        @Override
        public Brick getBrick() { return new OBrick(); }
        @Override
        public Brick getNextBrick() { return new OBrick(); }
        @Override
        public List<Brick> getNextBricks(int count) {
            return Collections.nCopies(count, new OBrick());
        }
    }

    @BeforeEach
    void setUp() {
        mockGui = new MockGuiController();

        // Updated to use the DI constructor with GameMode and StubBrickGenerator
        controller = new GameController(mockGui, GameMode.ADVENTURE, new StubBrickGenerator());
    }

    @Test
    void testInitialStateIsPlaying() {
        // In PlayingState, a DOWN event should move the brick
        int initialY = controller.getBoard().getViewData().getyPosition();

        controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));

        int newY = controller.getBoard().getViewData().getyPosition();
        assertNotEquals(initialY, newY, "In PlayingState, brick should move down.");
    }

    @Test
    void testPauseFreezesGame() {
        controller.togglePause();
        int initialY = controller.getBoard().getViewData().getyPosition();
        int initialScore = controller.getBoard().getScore().getScore();

        // Attempt to move/score while paused
        controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        controller.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));

        int newY = controller.getBoard().getViewData().getyPosition();
        int newScore = controller.getBoard().getScore().getScore();

        assertEquals(initialY, newY, "In PausedState, brick should NOT move.");
        assertEquals(initialScore, newScore, "In PausedState, score should NOT increase.");
    }

    @Test
    void testResumeUnfreezesGame() {
        // Pause then Unpause
        controller.togglePause(); // Paused
        controller.togglePause(); // Playing

        int initialY = controller.getBoard().getViewData().getyPosition();

        // Attempt move after resume
        controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));

        int newY = controller.getBoard().getViewData().getyPosition();
        assertNotEquals(initialY, newY, "After resuming, game should respond to input again.");
    }

    @Test
    void testGameOverLocksInput() {
        // Force Game Over
        controller.setState(controller.getGameOverState());

        int initialY = controller.getBoard().getViewData().getyPosition();

        // Attempt inputs
        controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        controller.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
        controller.togglePause(); // Should be ignored in Game Over

        int newY = controller.getBoard().getViewData().getyPosition();

        // Verify inputs were ignored
        assertEquals(initialY, newY, "In GameOverState, inputs must be ignored.");
    }

    @Test
    void testNewGameResetsToPlayingState() {
        controller.setState(controller.getGameOverState());
        controller.createNewGame();

        // Verify inputs work again
        int initialY = controller.getBoard().getViewData().getyPosition();
        controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        int newY = controller.getBoard().getViewData().getyPosition();

        assertNotEquals(initialY, newY, "New Game should transition back to PlayingState.");
    }
}