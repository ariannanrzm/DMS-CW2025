package com.comp2042.game.controller;

import com.comp2042.game.bricks.RandomBrickGenerator;
import com.comp2042.game.states.GameOverState;
import com.comp2042.game.states.GameState;
import com.comp2042.game.states.PausedState;
import com.comp2042.game.states.PlayingState;
import com.comp2042.ui.GuiController;
import com.comp2042.game.board.*;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.game.config.GameConfig;
import com.comp2042.game.logic.LevelManager;


/**
 * GameController handles only game logic and communication with the Board.
 */
public class GameController implements InputEventListener  {

    /** Game logic board */
    private final Board board;
    private final LevelManager levelManager;
    private final GuiController viewGuiController;
    private final GameState playingState;
    private final GameState pausedState;
    private final GameState gameOverState;
    private GameState currentState;

    public GameController(GuiController viewGuiController) {
        this.viewGuiController = viewGuiController;
        this.levelManager = new LevelManager();

        this.board = new SimpleBoard(
                GameConfig.get().getBoardHeight(),
                GameConfig.get().getBoardWidth(),
                new RandomBrickGenerator()
        );

        // Initialize States
        this.playingState = new PlayingState(this);
        this.pausedState = new PausedState(this);
        this.gameOverState = new GameOverState(this);

        // Default state
        this.currentState = playingState;

        this.viewGuiController.setGameController(this);
        this.viewGuiController.setEventListener(this);

        board.createNewBrick();

        this.viewGuiController.initGameView(
                board.getBoardMatrix(),
                board.getViewData(),
                levelManager.getCurrentSpeed()
        );

        this.viewGuiController.bindScore(board.getScore().scoreProperty());
        this.viewGuiController.bindLines(board.getScore().linesProperty());
        this.viewGuiController.bindLevel(levelManager.levelProperty());


        levelManager.levelProperty().addListener((obs, oldVal, newVal) -> {
            viewGuiController.updateGameSpeed(levelManager.getCurrentSpeed());
            viewGuiController.showLevelUpNotification(newVal.intValue());

        });
    }

    public void notifyLinesCleared(int count) {
        if (count > 0) {
            levelManager.onLinesCleared(count);
        }
    }


    public void setState(GameState state) {
        this.currentState = state;
    }

    public GameState getPlayingState() { return playingState; }
    public GameState getPausedState() { return pausedState; }
    public GameState getGameOverState() { return gameOverState; }

    // Getters for State classes to access Board/View
    public Board getBoard() {
        return board;
    }

    public GuiController getGuiController() {
        return viewGuiController;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        return currentState.handleDownEvent(event);
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        return currentState.handleLeftEvent(event);
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        return currentState.handleRightEvent(event);
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        return currentState.handleRotateEvent(event);
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        return currentState.handleHardDropEvent(event);
    }

    @Override
    public void createNewGame() {
        board.newGame();
        levelManager.reset();
        setState(getPlayingState());
        viewGuiController.resetGameView();
        viewGuiController.updateGameSpeed(levelManager.getCurrentSpeed());
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        return currentState.handleHoldEvent(event);
    }

    public void togglePause() {
        // Prevent pausing if the game is already over
        if (currentState == gameOverState) return;

        if (currentState == playingState) {
            setState(pausedState);
        } else if (currentState == pausedState) {
            setState(playingState);
            viewGuiController.showPauseMessage(false);
        }
    }

}
