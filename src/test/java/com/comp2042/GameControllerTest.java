package com.comp2042;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    @Test
    public void testSoftDropIncreasesScore() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        int initialScore = controller.getBoard().getScore().get();

        controller.onDownEvent(
                new MoveEvent(EventType.DOWN, EventSource.USER)
        );

        int updatedScore = controller.getBoard().getScore().get();
        assertEquals(initialScore + 1, updatedScore);
    }

    @Test
    public void testBrickSpawnsAfterLanding() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        for (int i = 0; i < 30; i++) {
            controller.onDownEvent(
                    new MoveEvent(EventType.DOWN, EventSource.AUTO)
            );
        }

        assertNotNull(controller.getBoard().getBoardMatrix());
    }

    @Test
    public void testGameOverTriggered() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        int[][] m = controller.getBoard().getBoardMatrix();
        for (int col = 0; col < 10; col++) {
            m[0][col] = 1;
        }

        controller.onDownEvent(
                new MoveEvent(EventType.DOWN, EventSource.AUTO)
        );

        assertTrue(mockGui.wasGameOverCalled());
    }
}
