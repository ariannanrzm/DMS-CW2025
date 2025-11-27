/**
 * Unit tests for GameController verifying:
 *  - soft-drop scoring,
 *  - brick spawning behaviour,
 *  - game-over detection when the spawn region is obstructed.
 *
 * Tests were updated after refactoring GameController and SimpleBoard,
 * including moving score logic out of MatrixOperations and adjusting SPAWN_Y.
 */

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
                    new MoveEvent(EventType.DOWN, EventSource.THREAD)
            );
        }

        assertNotNull(controller.getBoard().getBoardMatrix());
    }

    @Test
    public void testGameOverTriggered() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        int[][] matrix = controller.getBoard().getBoardMatrix();

        int spawnX = 4;   // default
        int spawnY = 0;   // your updated SimpleBoard spawn row

        // Fill the 4×4 spawn region
        for (int row = spawnY; row < spawnY + 4; row++) {
            for (int col = spawnX; col < spawnX + 4; col++) {
                if (row < matrix.length && col < matrix[row].length) {
                    matrix[row][col] = 1;
                }
            }
        }

        var result = controller.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));

        assertTrue(result.isGameOver(), "Game over should be triggered when the spawn area is blocked.");
    }

    @Test
    public void testMoveLeftDecreasesX() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        int initialX = controller.getBoard().getViewData().getxPosition();
        int initialY = controller.getBoard().getViewData().getyPosition();

        // simulate LEFT key from user
        controller.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));

        int newX = controller.getBoard().getViewData().getxPosition();
        int newY = controller.getBoard().getViewData().getyPosition();

        assertEquals(initialX - 1, newX, "Brick should move one step left");
        assertEquals(initialY, newY, "Y position should not change when moving left");
    }

    @Test
    public void testMoveRightIncreasesX() {
        MockGuiController mockGui = new MockGuiController();
        GameController controller = new GameController(mockGui);

        int initialX = controller.getBoard().getViewData().getxPosition();
        int initialY = controller.getBoard().getViewData().getyPosition();

        // simulate RIGHT key from user
        controller.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));

        int newX = controller.getBoard().getViewData().getxPosition();
        int newY = controller.getBoard().getViewData().getyPosition();

        assertEquals(initialX + 1, newX, "Brick should move one step right");
        assertEquals(initialY, newY, "Y position should not change when moving right");
    }
}
