package com.comp2042;

import com.comp2042.game.board.ClearRow;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.ui.GuiController;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;

public class MockGuiController extends GuiController {

    private boolean gameOverCalled = false;
    private boolean gameWonCalled = false;
    private int[][] latestBackground;
    private ViewData latestViewData;

    public MockGuiController() {
        // Call parent constructor indirectly
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        // Do nothing (override FXML/JavaFX initialization)
    }

    /**
     * Prevent attaching InputHandler (which uses JavaFX Events) to a null GamePanel.
     */
    @Override
    public void setGameController(GameController controller) {
        // Do nothing.
    }

    @Override
    public void setEventListener(InputEventListener eventListener) {
        super.setEventListener(eventListener);
    }

    /**
     * Updated to match the 3-argument signature in GuiController.
     */
    @Override
    public void initGameView(int[][] boardMatrix, ViewData viewData, double initialSpeed) {
        this.latestBackground = boardMatrix;
        this.latestViewData = viewData;
    }

    @Override
    public void refreshGameBackground(int[][] background) {
        this.latestBackground = background;
    }

    @Override
    public void refreshBrick(ViewData brick) {
        this.latestViewData = brick;
    }

    @Override
    public void bindScore(IntegerProperty integerProperty) { }

    @Override
    public void bindLines(IntegerProperty property) { }

    @Override
    public void bindLevel(IntegerProperty property) { }

    @Override
    public void updateGameSpeed(double delayMillis) { }

    @Override
    public void updateTimer(String timeString) { }

    // --- Stub out Notification/Animation methods to avoid Toolkit errors ---

    @Override
    public void showLevelUpNotification(int newLevel) { }

    @Override
    public void showChaosNotification(String message) { }

    @Override
    public void playHardDropBounce() { }

    @Override
    public void showNotificationIfRowsCleared(ClearRow clearRow) { }

    @Override
    public void resetTimeline() { }

    @Override
    public void showPauseMessage(boolean isPaused) { }

    @Override
    public void resetGameView() { }

    // Game State Handling

    @Override
    public void gameOver() {
        this.gameOverCalled = true;
    }

    @Override
    public void gameWon(int finalSeconds) {
        this.gameWonCalled = true;
    }

    @Override
    public void newGame(ActionEvent actionEvent) {
        this.gameOverCalled = false;
        this.gameWonCalled = false;
    }

    @Override
    public void pauseGame(ActionEvent actionEvent) { }


    // Test Helpers

    public boolean wasGameOverCalled() {
        return gameOverCalled;
    }

    public boolean wasGameWonCalled() {
        return gameWonCalled;
    }

    public int[][] getLatestBackground() {
        return latestBackground;
    }

    public ViewData getLatestViewData() {
        return latestViewData;
    }
}