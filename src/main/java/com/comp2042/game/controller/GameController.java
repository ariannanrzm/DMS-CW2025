package com.comp2042.game.controller;

import com.comp2042.game.bricks.BrickGenerator;
import com.comp2042.game.bricks.RandomBrickGenerator;
import com.comp2042.game.logic.HighScoreManager;
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
import com.comp2042.game.events.EventSource;
import javafx.animation.KeyFrame;
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
    private int chaosTimer = 0;
    private boolean controlsReversed = false;


    public GameController(GuiController viewGuiController, GameMode gameMode, BrickGenerator brickGenerator) {
        this.viewGuiController = viewGuiController;
        this.levelManager = new LevelManager();
        this.gameMode = gameMode;

        this.board = new SimpleBoard(
                GameConfig.get().getBoardHeight(),
                GameConfig.get().getBoardWidth(),
                brickGenerator
        );


        // Initialize States
        this.playingState = new PlayingState(this);
        this.pausedState = new PausedState(this);
        this.gameOverState = new GameOverState(this);

        // Default state
        this.currentState = playingState;

        this.viewGuiController.setGameController(this);
        this.viewGuiController.setEventListener(this);

        // Start Game
        board.createNewBrick();
        initializeView();
        setupLevelListeners();
        setupStopwatch();
    }


    /**
     * Convenience Constructor.
     * Use this for normal Gameplay (defaults to RandomBrickGenerator).
     */
    public GameController(GuiController viewGuiController, GameMode gameMode) {
        this(viewGuiController, gameMode, new RandomBrickGenerator());
    }

    private void initializeView() {
        this.viewGuiController.initGameView(
                board.getBoardMatrix(),
                board.getViewData(),
                levelManager.getCurrentSpeed()
        );
        this.viewGuiController.bindScore(board.getScore().scoreProperty());
        this.viewGuiController.bindLines(board.getScore().linesProperty());
        this.viewGuiController.bindLevel(levelManager.levelProperty());
    }

    private void setupLevelListeners() {
        levelManager.levelProperty().addListener((obs, oldVal, newVal) ->
                handleLevelChange(newVal.intValue())
        );
    }

    /**
     * Handles all logic triggered when the level changes.
     */
    private void handleLevelChange(int level) {
        // Check for Win Condition
        if (level > 6) {
            handleVictory();
            return;
        }

        // Update UI and Mechanics
        viewGuiController.updateGameSpeed(levelManager.getCurrentSpeed());
        viewGuiController.showLevelUpNotification(level);

        // Reset mechanics for new level
        garbageTimer = 0;

        applyLevelMechanics(level);
    }

    private void handleVictory() {
        HighScoreManager.tryUpdateFastestTime(secondsElapsed);
        HighScoreManager.tryUpdateHighScore(board.getScore().getScore());
        viewGuiController.gameWon(secondsElapsed);
        setState(gameOverState);
    }

    private void applyLevelMechanics(int level) {
        // Warning for Garbage Mode
        if (level == 3 || level == 4) {
            viewGuiController.showChaosNotification("LEVELS RISING..");
        }

        // Logic for Reverse Controls
        if (level == 5) {
            chaosTimer = 0;
            controlsReversed = true;
            viewGuiController.showChaosNotification("RANDOM CONTROLS");
        } else if (level == 6) {
            chaosTimer = 0;
            controlsReversed = true;
            viewGuiController.showChaosNotification("FINAL LEVEL!!");
        } else {
            // Reset controls if we aren't in a chaos level
            controlsReversed = false;
        }
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
                handleChaosMode();
            }
        }));
        this.stopwatchTimeline.setCycleCount(Timeline.INDEFINITE);
        this.stopwatchTimeline.play();
    }

    private void handleChaosMode() {
        // Only active in Level 5
        int lvl = levelManager.getCurrentLevel();
        if (lvl != 5 && lvl != 6) return;

        chaosTimer++;

        int cyclePosition = chaosTimer % 13;

        // Toggle controls based on timer
        if (cyclePosition < 3) {
            if (!controlsReversed) controlsReversed = true;
        } else {
            if (controlsReversed) controlsReversed = false;
        }
    }

    private void handleGarbageGeneration() {
        garbageTimer++;
        int currentLevel = levelManager.getCurrentLevel();
        int interval = getGarbageInterval(currentLevel);

        if (interval > 0 && garbageTimer >= interval) {
            triggerGarbageRow();
            garbageTimer = 0;
        } else if (interval == -1) {
            garbageTimer = 0; // Keep timer at 0 if no garbage this level
        }
    }

    /**
     * Returns the garbage generation interval in seconds for a given level.
     * Returns -1 if garbage generation is disabled for that level.
     */
    private int getGarbageInterval(int level) {
        return switch (level) {
            case 3 -> 15;
            case 4 -> 10;
            case 5, 6 -> 7;
            default -> -1;
        };
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

        if (gameMode == GameMode.ZEN) {
            return;
        }
        if (count > 0 && (gameMode == GameMode.ADVENTURE)) {
            levelManager.onLinesCleared(count);
        }
    }


    public void setState(GameState state) {
        this.currentState = state;

        if (state == gameOverState) {
            int currentScore = board.getScore().getScore();
            HighScoreManager.tryUpdateHighScore(currentScore);

            if (stopwatchTimeline != null) {
                stopwatchTimeline.stop();
            }
        }
    }

    public GameState getPlayingState() { return playingState; }
    public GameState getPausedState() { return pausedState; }
    public GameState getGameOverState() { return gameOverState; }
    public Board getBoard() {return board;}
    public GuiController getGuiController() {return viewGuiController;}
    public int getSecondsElapsed() { return secondsElapsed;}

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
        DownData data = currentState.handleHardDropEvent(event);

        // Only bounce for hard drops
        if (data != null && event.getEventSource() == EventSource.USER) {
            viewGuiController.playHardDropBounce();
        }

        return data;
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
        if (currentState == gameOverState) return;

        if (currentState == playingState) {
            setState(pausedState);
            viewGuiController.showPauseMessage(true);

            if (stopwatchTimeline != null) stopwatchTimeline.pause(); // Pause Timer

        } else if (currentState == pausedState) {
            setState(playingState);
            viewGuiController.showPauseMessage(false);

            if (stopwatchTimeline != null) stopwatchTimeline.play(); // Resume Timer
        }
    }

}
