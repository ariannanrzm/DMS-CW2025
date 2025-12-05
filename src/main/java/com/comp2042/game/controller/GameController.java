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
import com.comp2042.game.config.GameMode;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
    private final GameMode gameMode;
    private int linesSinceLastGarbage = 0;
    private Timeline stopwatchTimeline;
    private int secondsElapsed;
    private int garbageTimer = 0;
    private boolean controlsReversed = false;


    public GameController(GuiController viewGuiController, GameMode gameMode) {
        this.viewGuiController = viewGuiController;
        this.levelManager = new LevelManager();
        this.gameMode = gameMode;

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

        // Listen for Level Changes
        levelManager.levelProperty().addListener((obs, oldVal, newVal) -> {
            int level = newVal.intValue();
            viewGuiController.updateGameSpeed(levelManager.getCurrentSpeed());
            viewGuiController.showLevelUpNotification(level);

            // Reset garbage timer when entering a new level
            garbageTimer = 0;

            // trigger reverse controls
            if (level == 5) {
                controlsReversed = true;
                viewGuiController.showChaosNotification("CHAOS MODE");
                System.out.println("CHAOS MODE STARTED");
            } else {
                if (controlsReversed) {
                    controlsReversed = false;
                    viewGuiController.showChaosNotification("CHAOS MODE ENDED");
                    System.out.println("CHAOS MODE ENDED");
                }
            }
        });
        setupStopwatch();

        
    }

    public boolean isControlsReversed() {
        return controlsReversed;
    }

    private void setupStopwatch() {
        this.secondsElapsed = 0;
        this.stopwatchTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed++;
            updateTimeLabel();

            if (currentState == playingState && gameMode == GameMode.ADVENTURE) {
                handleGarbageGeneration();
            }
        }));
        this.stopwatchTimeline.setCycleCount(Timeline.INDEFINITE);
        this.stopwatchTimeline.play();
    }

    private void handleGarbageGeneration() {
        garbageTimer++;
        int currentLevel = levelManager.getCurrentLevel();

        // Level 3: Garbage every 15 seconds
        if (currentLevel == 3) {
            if (garbageTimer >= 15) {
                triggerGarbageRow();
                garbageTimer = 0;
            }
        }
        // Level 4+: Garbage every 10 seconds
        else if (currentLevel >= 4) {
            if (garbageTimer >= 10) {
                triggerGarbageRow();
                garbageTimer = 0;
            }
        }
        // Reset timer for lower levels to prevent accumulation before reaching level 3
        else {
            garbageTimer = 0;
        }
    }

    private void triggerGarbageRow() {
        board.addGarbageRow();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    private void updateTimeLabel() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        viewGuiController.updateTimer(formattedTime);
    }

    public void notifyLinesCleared(int count) {
        if (count > 0 && (gameMode == GameMode.ADVENTURE)) {
            levelManager.onLinesCleared(count);
        }
    }


    public void setState(GameState state) {
        this.currentState = state;

        if (state == gameOverState && stopwatchTimeline != null) {
            stopwatchTimeline.stop();
        }
    }

    public GameState getPlayingState() { return playingState; }
    public GameState getPausedState() { return pausedState; }
    public GameState getGameOverState() { return gameOverState; }
    public Board getBoard() {return board;}
    public GuiController getGuiController() {return viewGuiController;}

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
        linesSinceLastGarbage = 0;
        controlsReversed = false;
        setState(getPlayingState());

        // Reset and Restart Stopwatch
        if (stopwatchTimeline != null) {
            stopwatchTimeline.stop();
        }
        setupStopwatch();

        viewGuiController.resetGameView();
        viewGuiController.updateGameSpeed(levelManager.getCurrentSpeed());
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) { return currentState.handleHoldEvent(event); }

    public void togglePause() {
        // Prevent pausing if the game is already over
        if (currentState == gameOverState) return;

        if (currentState == playingState) {
            setState(pausedState);
            if (stopwatchTimeline != null) stopwatchTimeline.pause(); // Pause Timer
        } else if (currentState == pausedState) {
            setState(playingState);
            viewGuiController.showPauseMessage(false);
            if (stopwatchTimeline != null) stopwatchTimeline.play(); // Resume Timer
        }
    }
}
