package com.comp2042;

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
    public void bindScore(IntegerProperty integerProperty) {
        // Skip GUI binding
    }

    @Override
    public void gameOver() {
        this.gameOverCalled = true;
    }

    @Override
    public void newGame(ActionEvent actionEvent) {
        // Skip GUI new game behavior
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
