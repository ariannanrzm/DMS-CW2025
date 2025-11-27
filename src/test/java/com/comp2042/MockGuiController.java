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
    private int[][] latestBackground;
    private ViewData latestViewData;

    public MockGuiController() {
        // Call parent constructor indirectly; we don't need FXML initialization
        // and we won't be using JavaFX components in tests.
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        // Do nothing (override FXML/JavaFX initialization)
    }

    /**
     * CRITICAL FIX:
     * We override this method to prevent the parent class from executing:
     * inputHandler.attachTo(gamePanel);
     * Since 'gamePanel' is null in this mock, the parent method causes a NullPointerException.
     */
    @Override
    public void setGameController(GameController controller) {
        // Do nothing. We don't need the real InputHandler in unit tests.
    }

    @Override
    public void setEventListener(InputEventListener eventListener) {
        super.setEventListener(eventListener);
    }

    @Override
    public void initGameView(int[][] boardMatrix, ViewData viewData) {
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
    public void bindScore(IntegerProperty integerProperty) {
        // Skip GUI binding
    }

    @Override
    public void gameOver() {
        this.gameOverCalled = true;
    }

    @Override
    public void resetTimeline() {
        // Do nothing during tests to avoid "Toolkit not initialized" errors
    }

    @Override
    public void showNotificationIfRowsCleared(ClearRow clearRow) {
        // Do nothing during tests
    }

    @Override
    public void newGame(ActionEvent actionEvent) {
        // Reset internal test state
        this.gameOverCalled = false;
    }

    @Override
    public void pauseGame(ActionEvent actionEvent) {
        // Skip GUI pause behavior
    }

    // Helpers for tests:
    public boolean wasGameOverCalled() {
        return gameOverCalled;
    }

    public int[][] getLatestBackground() {
        return latestBackground;
    }

    public ViewData getLatestViewData() {
        return latestViewData;
    }
}