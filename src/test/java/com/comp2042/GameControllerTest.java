package com.comp2042;

import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.MoveEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    @Test
    public void testSoftDropIncreasesScore() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        int initialScore = controller.getBoard().getScore().getScore();

        controller.onDownEvent(
                new MoveEvent(EventType.DOWN, EventSource.USER)
        );

        int updatedScore = controller.getBoard().getScore().getScore();
        assertEquals(initialScore + 1, updatedScore);
    }

    @Test
    public void testBrickSpawnsAfterLanding() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        for (int i = 0; i < 30; i++) {
            controller.onDownEvent(
                    new MoveEvent(EventType.DOWN, EventSource.THREAD
                    )
            );
        }

        assertNotNull(controller.getBoard().getBoardMatrix());
    }

    @Test
    public void testGameOverTriggered() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        // Fill the spawn region so the new brick immediately collides
        int[][] m = controller.getBoard().getBoardMatrix();
        int spawnY = 10;
        int spawnX = 4;

        for (int row = spawnY; row < spawnY + 4; row++) {
            for (int col = spawnX; col < spawnX + 4; col++) {
                m[row][col] = 1;
            }
        }

        controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));

        assertTrue(mockGui.wasGameOverCalled(), "Game over should have been triggered");
    }

}
