package com.comp2042;

import com.comp2042.game.bricks.*;
import com.comp2042.game.config.GameMode;
import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.MoveEvent;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized, ignore
        }
    }

    // A Stub Generator that ALWAYS returns an O-Brick (Square)
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

    @Test
    public void testSoftDropIncreasesScore() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui, GameMode.ADVENTURE, new StubBrickGenerator());

        int initialScore = controller.getBoard().getScore().getScore();
        controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));

        assertEquals(initialScore + 1, controller.getBoard().getScore().getScore());
    }

    @Test
    public void testGameOverTriggered() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui, GameMode.ADVENTURE, new StubBrickGenerator());

        int[][] matrix = controller.getBoard().getBoardMatrix();

        int blockRow = 2;
        int blockCol = 4;

        // Block the path immediately below the spawn
        matrix[blockRow][blockCol] = 1;

        // This simulates: "Try to move down" -> "Blocked at Row 2" -> "Lock Brick" -> "Spawn New" -> "Game Over"
        var result = controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));

        assertTrue(result.isGameOver(), "Game should be over if the path is blocked immediately.");
    }
}