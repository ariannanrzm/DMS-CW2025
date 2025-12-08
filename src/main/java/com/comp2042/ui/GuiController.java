package com.comp2042.ui;

import com.comp2042.game.board.ClearRow;
import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.controller.GameController;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.game.config.GameConfig;
import com.comp2042.game.config.GameMode;
import com.comp2042.util.SoundManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.animation.*;
import javafx.scene.shape.Rectangle;
import com.comp2042.util.MusicManager;

/**
 * The main view controller for the gameplay scene.
 * It manages the grid display, next brick previews, score labels, animations,
 * and handles UI updates triggered by the game loop.
 */
public class GuiController implements Initializable {

    private final Paint[] paintCache = new Paint[8];
    private final Border BLOCK_BORDER = new Border(new BorderStroke(
            Color.web("rgba(255,255,255,0.4)"), Color.TRANSPARENT, Color.TRANSPARENT, Color.web("rgba(255,255,255,0.4)"),
            BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY
    ));

    @FXML private GridPane gamePanel;
    @FXML private StackPane groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private Label scoreLabel;
    @FXML private Label linesLabel;
    @FXML private VBox nextBrickContainer;
    @FXML private VBox holdBrickContainer;
    @FXML private Label levelLabel;
    @FXML private Label timeLabel;
    @FXML private StackPane centerNotificationOverlay;
    @FXML private Rectangle redFlashOverlay;
    @FXML private Pane gameBoard;
    @FXML private StackPane scoreOverlay;
    @FXML private StackPane pauseMenu;
    @FXML private Label highScoreLabel;
    @FXML private Label bestTimeLabel;
    @FXML private VBox levelContainer;
    @FXML private VBox linesContainer;
    @FXML private HBox bestTimeContainer;
    @FXML private HBox highScoreContainer;
    @FXML private VBox scoreContainer;
    @FXML private StackPane rootPane;
    @FXML private StackPane scalableContainer;
    @FXML private StackPane howToOverlayInGame;


    private StackPane[][] displayMatrix;
    private StackPane[][] rectangles;
    private InputEventListener eventListener;
    private Timeline timeLine;
    private InputHandler inputHandler;
    private GameController gameController;
    private GridPane ghostPanel;
    private StackPane[][] ghostRectangles;
    private SequentialTransition hardDropBounce;
    private GameMode currentMode;


    /**
     * Initializes the controller class.
     * Sets up the game panel focus, loads fonts, initializes the color cache,
     * and sets up responsiveness for window resizing.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unrelated.
     * @param resources The resources used to localize the root object, or null if unrelated.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (centerNotificationOverlay != null) {
            centerNotificationOverlay.getChildren().clear();
        }
        if (groupNotification != null) {
            groupNotification.getChildren().clear();
        }

        Font.loadFont(getClass().getClassLoader().getResourceAsStream("PressStart2P.ttf"), 38);        gamePanel.getStyleClass().add("game-grid");
        brickPanel.toFront();
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        for (int i = 0; i < 8; i++) {
            Color c;
            switch (i) {
                case 1 -> c = Color.web("#8BE9FD"); // Cyan
                case 2 -> c = Color.web("#6272A4"); // Slate
                case 3 -> c = Color.web("#FFB86C"); // Orange
                case 4 -> c = Color.web("#F1FA8C"); // Yellow
                case 5 -> c = Color.web("#50FA7B"); // Green
                case 6 -> c = Color.web("#BD93F9"); // Purple
                case 7 -> c = Color.web("#FF79C6"); // Pink
                default -> c = Color.TRANSPARENT;
            }
           paintCache[i] = c;

        }

        if (highScoreLabel != null) {
            highScoreLabel.setText("" + com.comp2042.game.logic.HighScoreManager.getHighScore());
        }
        refreshBestTime();

        if (pauseMenu != null) {
            Platform.runLater(() -> com.comp2042.util.SoundManager.getInstance().registerButtons(pauseMenu));
        }

        if (howToOverlayInGame != null) {
            howToOverlayInGame.setVisible(false);
            howToOverlayInGame.setOpacity(0.0);
            howToOverlayInGame.setMouseTransparent(true);
        }

        Platform.runLater(() -> {
            if (rootPane != null && rootPane.getScene() != null) {
                Stage stage = (Stage) rootPane.getScene().getWindow();

                stage.widthProperty().addListener((obs, oldVal, newVal) -> scaleGame(stage));
                stage.heightProperty().addListener((obs, oldVal, newVal) -> scaleGame(stage));

                scaleGame(stage);
            }
        });
    }


    /**
     * Scales the game interface based on the current window size to maintain
     * aspect ratio and visibility.
     *
     * @param stage The primary stage of the application.
     */
    private void scaleGame(Stage stage) {
        if (scalableContainer == null) return;

        final double DESIGN_WIDTH = 650;
        final double DESIGN_HEIGHT = 600;

        double windowWidth = stage.getScene().getWidth();
        double windowHeight = stage.getScene().getHeight();

        double scaleX = windowWidth / DESIGN_WIDTH;
        double scaleY = windowHeight / DESIGN_HEIGHT;

        double scale = Math.min(scaleX, scaleY);

        if (scale < 0.5) scale = 0.5;

        scalableContainer.setScaleX(scale);
        scalableContainer.setScaleY(scale);
    }

    /**
     * Formats a total number of seconds into a "MM:SS" string format.
     *
     * @param totalSeconds The time in seconds.
     * @return A formatted string representation of the time.
     */
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Retrieves the fastest completion time from the high score manager
     * and updates the Best Time label in the UI.
     */
    private void refreshBestTime() {
        if (bestTimeLabel != null) {
            int best = com.comp2042.game.logic.HighScoreManager.getFastestTime();
            String text = (best == Integer.MAX_VALUE) ? "--:--" : formatTime(best);
            bestTimeLabel.setText(text);
        }
    }

    /**
     * Links this UI controller to the main GameController and initializes the input handler.
     *
     * @param controller The main GameController instance.
     */
    public void setGameController(GameController controller) {
        this.gameController = controller;
        this.inputHandler = new InputHandler(controller);
        inputHandler.attachTo(gamePanel);
    }

    /**
     * Starts a new game session in the specified mode.
     * Initializes music, resets UI components, and instantiates the GameController.
     *
     * @param mode The game mode to start.
     */
    public void startGame(GameMode mode) {
        this.currentMode = mode; // Save mode

        if (mode == GameMode.ZEN) {
            MusicManager.getInstance().playMusic("zen.mp3", true);
        } else if (mode == GameMode.ADVENTURE) {
            MusicManager.getInstance().playMusic("adventure.mp3", true);
        }

        // Stop any existing game loop first
        if (timeLine != null) {
            timeLine.stop();
            timeLine = null;
        }

        if (rootPane != null) {
            rootPane.getStyleClass().remove("zen-mode");

            if (mode == GameMode.ZEN) {
                rootPane.getStyleClass().add("zen-mode");
            }
        }

        if (highScoreLabel != null) {
            highScoreLabel.setText("" + com.comp2042.game.logic.HighScoreManager.getHighScore());
        }
        refreshBestTime();

        // zen mode logic
        boolean isZen = (mode == GameMode.ZEN);
        setVisible(levelContainer, !isZen);
        setVisible(linesContainer, !isZen);
        setVisible(bestTimeContainer, !isZen);
        setVisible(highScoreContainer, !isZen);
        setVisible(scoreContainer, !isZen);

        new GameController(this, mode);
        gamePanel.requestFocus();
    }

    /**
     * Helper method to toggle the visibility and management state of a JavaFX Pane.
     *
     * @param pane    The pane to modify.
     * @param visible True to show the pane, false to hide and unmanage it.
     */
    private void setVisible(Pane pane, boolean visible) {
        if (pane != null) {
            pane.setVisible(visible);
            pane.setManaged(visible);
        }
    }

    /**
     * Updates the time label with the current game duration.
     *
     * @param timeString The formatted time string to display.
     */
    public void updateTimer(String timeString) {
        if (timeLabel != null) {
            timeLabel.setText(timeString);
        }
    }

    /**
     * Restarts the game loop timeline.
     */
    public void resetTimeline() {
        if (timeLine != null) {
            timeLine.stop();
            timeLine.play();
        }
    }

    /**
     * Initializes the visual grid of the game board.
     * Creates the StackPanes that represent the cells of the Tetris board and next-brick previews.
     *
     * @param boardMatrix  The initial state of the logical board.
     * @param brick        The view data for the current active brick.
     * @param initialSpeed The starting speed of the game loop.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick, double initialSpeed) {
        displayMatrix = new StackPane[boardMatrix.length][boardMatrix[0].length];

        int HIDDEN_ROWS = GameConfig.get().getHiddenRows();
        int BRICK_SIZE = GameConfig.get().getBrickSize();

        for (int i = HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                StackPane r = new StackPane();
                r.setPrefSize(BRICK_SIZE, BRICK_SIZE);
                r.setBackground(Background.EMPTY);

                displayMatrix[i][j] = r;
                gamePanel.add(r, j, i - HIDDEN_ROWS);
            }
        }

        initGhostPanel(BRICK_SIZE);
        initBrickPanel(brick, BRICK_SIZE);
        Platform.runLater(() -> updateBrickPanelPosition(brick));
        refreshNextBricks(brick.getNextBricks());
        refreshHeldBrick(brick.getHeldBrickData());

        updateGameSpeed(initialSpeed);
    }

    /**
     * Initializes the "ghost" brick panel which shows where the active brick will land.
     *
     * @param brickSize The size of the individual block cells.
     */
    private void initGhostPanel(int brickSize) {
        ghostPanel = new GridPane();
        ghostPanel.setVgap(1);
        ghostPanel.setHgap(1);

        Pane parent = (Pane) brickPanel.getParent();
        parent.getChildren().add(parent.getChildren().indexOf(brickPanel), ghostPanel);
        ghostRectangles = new StackPane[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                StackPane r = new StackPane();
                r.setPrefSize(brickSize, brickSize);
                r.getStyleClass().add("game-block");
                r.setBackground(new Background(new BackgroundFill(Color.web("#ffffff", 0.2), CornerRadii.EMPTY, Insets.EMPTY)));
                r.setVisible(false);
                ghostRectangles[i][j] = r;
                ghostPanel.add(r, j, i);
            }
        }
    }

    /**
     * Initializes the panel responsible for displaying the currently active moving brick.
     *
     * @param brick     The view data containing the brick's shape.
     * @param brickSize The size of the individual block cells.
     */
    private void initBrickPanel(ViewData brick, int brickSize) {
        rectangles = new StackPane[brick.getBrickData().length][brick.getBrickData()[0].length];

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                StackPane r = new StackPane();
                r.setPrefSize(brickSize, brickSize);
                r.getStyleClass().add("game-block");
                setRectangleData(brick.getBrickData()[i][j], r);
                rectangles[i][j] = r;
                brickPanel.add(r, j, i);
            }
        }
    }


    /**
     * Displays a floating notification when the player levels up.
     *
     * @param newLevel The new level number reached.
     */
    public void showLevelUpNotification(int newLevel) {
        if (currentMode == GameMode.ZEN) return;
        NotificationPanel notification = new NotificationPanel("LEVEL UP!");
        centerNotificationOverlay.getChildren().add(notification);
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(500), notification);
        moveUp.setByY(-80);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notification);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ParallelTransition animation = new ParallelTransition(moveUp, fadeOut);
        animation.setOnFinished(e -> centerNotificationOverlay.getChildren().remove(notification));
        animation.play();
    }

    /**
     * Displays a notification for "Chaos Mode" events.
     *
     * @param message The text message to display.
     */
    public void showChaosNotification(String message) {
        NotificationPanel notification = new NotificationPanel(message);
        notification.setStyleClass("centerMessage");

        centerNotificationOverlay.getChildren().add(notification);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), notification);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> centerNotificationOverlay.getChildren().remove(notification));

        fadeOut.play();
    }

    /**
     * Checks if lines were cleared and displays appropriate notifications (Single, Double, Tetris).
     * Also handles combo notifications and sound effects.
     *
     * @param clearRow The object containing data about cleared lines and scores.
     */
    public void showNotificationIfRowsCleared(ClearRow clearRow) {
        if (currentMode == GameMode.ZEN) return;
        String text = switch (clearRow.getLinesRemoved()) {
            case 1 -> "SINGLE";
            case 2 -> "DOUBLE";
            case 3 -> "TRIPLE";
            case 4 -> "TETRIS";
            default -> "";
        };

        if (!text.isEmpty()) {
            NotificationPanel notification = new NotificationPanel(text);
            groupNotification.getChildren().add(notification);
            notification.showScore(groupNotification.getChildren());
        }

        //  Check for Combo
        int currentCombo = gameController.getBoard().getScore().getComboCount();

        if (currentCombo > 0) {
            com.comp2042.util.SoundManager.getInstance().playBonus();

            // Delay the combo notification slightly or stack it
            NotificationPanel comboNotification = new NotificationPanel("COMBO x" + currentCombo);
            comboNotification.setTranslateY(30);
            groupNotification.getChildren().add(comboNotification);
            comboNotification.showScore(groupNotification.getChildren());
        }
        if (clearRow.getLinesRemoved() == 4) {
            com.comp2042.util.SoundManager.getInstance().playBonus();
        }

        // SHOW SCORE POPUP (Small floating number)
        if (clearRow.getScoreBonus() > 0) {
            showScorePopup(clearRow.getScoreBonus());
        }
    }

    /**
     * Shows a small floating score number in the center overlay.
     *
     * @param score The amount of points to display.
     */
    private void showScorePopup(int score) {
        Label scoreLabel = new Label("+" + score);
        scoreLabel.getStyleClass().add("scorePopup");
        scoreLabel.setMouseTransparent(true);

        scoreOverlay.getChildren().add(scoreLabel);
        TranslateTransition floatUp = new TranslateTransition(Duration.millis(1000), scoreLabel);
        floatUp.setByY(-50);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), scoreLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ParallelTransition animation = new ParallelTransition(floatUp, fadeOut);
        animation.setOnFinished(e -> centerNotificationOverlay.getChildren().remove(scoreLabel));
        animation.play();
    }

    /**
     * Updates the X and Y coordinates of the active brick panel and the ghost panel
     * to match the logic board state.
     *
     * @param brick The current view data of the active brick.
     */
    private void updateBrickPanelPosition(ViewData brick) {
        int BRICK_SIZE = GameConfig.get().getBrickSize();
        int TOP_OFFSET = GameConfig.get().getTopOffset();

        // Correct the visual offset when brick falls
        final int CELL_STEP = BRICK_SIZE + 1;

        brickPanel.setLayoutX(gamePanel.getLayoutX()
                + brick.getxPosition() * CELL_STEP
        );

        brickPanel.setLayoutY(TOP_OFFSET + gamePanel.getLayoutY()
                + brick.getyPosition() * CELL_STEP);

        if (ghostPanel != null) {
            ghostPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * CELL_STEP);
            ghostPanel.setLayoutY(TOP_OFFSET + gamePanel.getLayoutY() + brick.getGhostYPosition() * CELL_STEP);
        }
    }

    /**
     * Retrieves the color associated with a specific block ID.
     *
     * @param i The block ID/color index.
     * @return The JavaFX Paint object for the block.
     */
    private Paint getFillColor(int i) {
        if (i >= 0 && i < paintCache.length) {
            return paintCache[i];
        }
        return Color.TRANSPARENT;
    }

    /**
     * Styles a single grid cell with the appropriate color and border.
     *
     * @param color The color index of the block.
     * @param r     The StackPane representing the grid cell.
     */
    private void setRectangleData(int color, StackPane r) {
        Paint fill = getFillColor(color);
        r.setBackground(new Background(new BackgroundFill(fill, CornerRadii.EMPTY, Insets.EMPTY)));
        if (color == 0) {
            r.setBorder(Border.EMPTY);
        } else {
            r.setBorder(BLOCK_BORDER);
        }    }

    /**
     * Refreshes the visual state of the active brick, including its position,
     * ghost position, and shape.
     *
     * @param brick The current ViewData containing brick information.
     */
    public void refreshBrick(ViewData brick) {
        updateBrickPanelPosition(brick);
        refreshNextBricks(brick.getNextBricks());
        refreshHeldBrick(brick.getHeldBrickData());

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                int type = brick.getBrickData()[i][j];
                if (type != 0) {
                    Paint originalPaint = getFillColor(type);
                    if (originalPaint instanceof Color) {
                        Color c = (Color) originalPaint;
                        Color ghostColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.3);
                        ghostRectangles[i][j].setBackground(new Background(new BackgroundFill(ghostColor, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                    ghostRectangles[i][j].setVisible(true);
                } else {
                    ghostRectangles[i][j].setVisible(false);
                }
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
        }
    }

    /**
     * Updates the "Next Brick" preview container with the upcoming pieces.
     *
     * @param nextBricks A list of matrices representing the shapes of the next bricks.
     */
    private void refreshNextBricks(List<int[][]> nextBricks) {
        // 1. Keep the Title Label (index 0)
        while (nextBrickContainer.getChildren().size() > 1) {
            nextBrickContainer.getChildren().remove(1);
        }

        // 2. FIX: Reset the label height so it doesn't push bricks down
        if (!nextBrickContainer.getChildren().isEmpty()
                && nextBrickContainer.getChildren().get(0) instanceof Label label) {
            label.setPrefHeight(-1); // -1 = USE_COMPUTED_SIZE (Auto)
            label.setMinHeight(-1);
        }

        int PREVIEW_SIZE = 15;

        for (int[][] matrix : nextBricks) {
            GridPane previewPane = new GridPane();
            previewPane.setAlignment(Pos.CENTER);
            previewPane.setHgap(1);
            previewPane.setVgap(1);

            int renderRow = 0;

            for (int row = 0; row < matrix.length; row++) {
                boolean rowHasBlock = false;
                // Check if this row is empty
                for (int col = 0; col < matrix[row].length; col++) {
                    if (matrix[row][col] != 0) {
                        rowHasBlock = true;
                        break;
                    }
                }

                // Only add the row if it contains parts of the brick
                if (rowHasBlock) {
                    for (int col = 0; col < matrix[row].length; col++) {
                        StackPane r = new StackPane();
                        r.setPrefSize(PREVIEW_SIZE, PREVIEW_SIZE);

                        if (matrix[row][col] != 0) {
                            r.getStyleClass().add("game-block");
                            r.setBackground(new Background(new BackgroundFill(getFillColor(matrix[row][col]), CornerRadii.EMPTY, Insets.EMPTY)));
                        } else {
                            // Add transparent spacer to maintain 4-column alignment
                            r.setBackground(Background.EMPTY);
                        }
                        previewPane.add(r, col, renderRow);
                    }
                    renderRow++;
                }
            }
            nextBrickContainer.getChildren().add(previewPane);
        }
    }

    /**
     * Updates the "Hold" container to display the currently held brick.
     *
     * @param matrix The shape matrix of the held brick.
     */
    private void refreshHeldBrick(int[][] matrix) {
        // 1. Keep the Title Label (index 0)
        while (holdBrickContainer.getChildren().size() > 1) {
            holdBrickContainer.getChildren().remove(1);
        }

        // 2. FIX: Reset the label height
        if (!holdBrickContainer.getChildren().isEmpty()
                && holdBrickContainer.getChildren().get(0) instanceof Label label) {
            label.setPrefHeight(-1);
            label.setMinHeight(-1);
        }

        if (matrix == null) return;

        int PREVIEW_SIZE = 15;
        GridPane previewPane = new GridPane();
        previewPane.setAlignment(Pos.CENTER);
        previewPane.setHgap(1);
        previewPane.setVgap(1);

        int renderRow = 0;

        for (int row = 0; row < matrix.length; row++) {
            boolean rowHasBlock = false;
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] != 0) {
                    rowHasBlock = true;
                    break;
                }
            }

            if (rowHasBlock) {
                for (int col = 0; col < matrix[row].length; col++) {
                    StackPane r = new StackPane();
                    r.setPrefSize(PREVIEW_SIZE, PREVIEW_SIZE);

                    if (matrix[row][col] != 0) {
                        r.getStyleClass().add("game-block");
                        r.setBackground(new Background(new BackgroundFill(getFillColor(matrix[row][col]), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else {
                        r.setBackground(Background.EMPTY);
                    }
                    previewPane.add(r, col, renderRow);
                }
                renderRow++;
            }
        }
        holdBrickContainer.getChildren().add(previewPane);
    }

    /**
     * Redraws the static game board background based on the logical grid state.
     *
     * @param board The 2D array representing the board's locked blocks.
     */
    public void refreshGameBackground(int[][] board) {
        int HIDDEN_ROWS = GameConfig.get().getHiddenRows();

        for (int i = HIDDEN_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Handles the logic associated with the periodic "move down" event (game tick).
     * Updates the board, checks for game over, and refreshes the UI.
     *
     * @param event The move event triggered by the timeline.
     */
    private void moveDown(MoveEvent event) {
        DownData downData = eventListener.onDownEvent(event);

        if (downData != null) {
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                showNotificationIfRowsCleared(downData.getClearRow());
            }
            refreshGameBackground(downData.getBoardMatrix());

            // Only refresh the brick if game is not over
            if(!downData.isGameOver()) {
                refreshBrick(downData.getViewData());
            }
        }

    }

    /**
     * Registers the listener that handles game input events.
     *
     * @param listener The InputEventListener implementation (usually GameController).
     */
    public void setEventListener(InputEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Binds the UI score label to the score property in the game logic.
     *
     * @param property The IntegerProperty representing the score.
     */
    public void bindScore(IntegerProperty property) {
        scoreLabel.textProperty().bind(property.asString("%d"));
    }

    /**
     * Binds the UI lines label to the lines-cleared property in the game logic.
     *
     * @param property The IntegerProperty representing cleared lines.
     */
    public void bindLines(IntegerProperty property) {
        linesLabel.textProperty().bind(property.asString("%d"));
    }

    /**
     * Handles the victory state, plays the win sound, and transitions to the game over screen.
     *
     * @param finalSeconds The total time taken to complete the game.
     */
    public void gameWon(int finalSeconds) {
        MusicManager.getInstance().playMusic("win.mp3", false);
        timeLine.stop();
        switchToGameOverScene(true, finalSeconds);    }


    /**
     * Handles the game over state, plays animations and sounds, and transitions to the result screen.
     */
    public void gameOver() {

        if (currentMode == GameMode.ZEN) {
            timeLine.stop();
            gameController.createNewGame();
            return;
        }
        MusicManager.getInstance().playMusic("gameover.mp3", false);
        timeLine.stop();

        FadeTransition flash = new FadeTransition(Duration.millis(150), redFlashOverlay);
        flash.setFromValue(0.0);
        flash.setToValue(0.6);
        flash.setCycleCount(2);
        flash.setAutoReverse(true);

        FadeTransition fadeBoard = new FadeTransition(Duration.millis(600), gameBoard);
        fadeBoard.setToValue(0.3);

        ScaleTransition shrinkBoard = new ScaleTransition(Duration.millis(600), gameBoard);
        shrinkBoard.setToX(0.9);
        shrinkBoard.setToY(0.9);

        ParallelTransition boardDeath = new ParallelTransition(fadeBoard, shrinkBoard);

        SequentialTransition sequence = new SequentialTransition(flash, boardDeath);
        int finalSeconds = (gameController != null) ? gameController.getSecondsElapsed() : 0;
        sequence.setOnFinished(e -> switchToGameOverScene(false, finalSeconds));

        sequence.play();
    }

    /**
     * Loads and switches the scene to the Game Over / Victory screen.
     *
     * @param isVictory    True if the player won, false if they lost.
     * @param finalSeconds The duration of the game session.
     */
    private void switchToGameOverScene(boolean isVictory, int finalSeconds) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameOver.fxml"));
            Parent gameOverRoot = loader.load();

            GameOverController controller = loader.getController();

            // Parse score safely
            int finalScore = 0;
            try {
                if (scoreLabel != null) {
                    finalScore = Integer.parseInt(scoreLabel.getText());
                }
            } catch (NumberFormatException ignored) {}

            controller.setScore(finalScore);
            controller.setGameTime(finalSeconds);

            if (isVictory) {
                controller.setTitle("YOU WON!", Color.web("#50FA7B"));
            } else {
                controller.setTitle("GAME OVER", Color.RED);
            }
            controller.animateEntry();

            Stage stage = (Stage) gamePanel.getScene().getWindow();
            Scene scene = new Scene(gameOverRoot, 650, 600);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load GameOver.fxml");
        }
    }

    /**
     * Resets the view for a new game.
     */
    public void resetGameView() {
        brickPanel.setVisible(true);
        if (ghostPanel != null) {
            ghostPanel.setVisible(true);
        }
        gamePanel.requestFocus();
        timeLine.play();
    }

    public void newGame(ActionEvent e) {
        eventListener.createNewGame();
    }
    public void pauseGame(ActionEvent e) {
        gamePanel.requestFocus();
    }

    /**
     * Toggles the visibility of the pause menu overlay and manages the game timeline state.
     *
     * @param isPaused True to show the pause menu and stop time, false to hide it and resume.
     */
    public void showPauseMessage(boolean isPaused) {
        if (isPaused) {
            pauseMenu.setVisible(true);
            pauseMenu.toFront();
            timeLine.pause();
        } else {
            pauseMenu.setVisible(false);
            timeLine.play();
            gamePanel.requestFocus();
        }
    }

    /**
     * Binds the UI level label to the level property in the game logic.
     *
     * @param property The IntegerProperty representing the current level.
     */
    public void bindLevel(IntegerProperty property) {
        levelLabel.textProperty().bind(property.asString("%d"));
    }

    /**
     * Updates the game loop speed by creating a new Timeline with the specified delay.
     *
     * @param delayMillis The delay between game ticks in milliseconds.
     */
    public void updateGameSpeed(double delayMillis) {
        if (timeLine != null) {
            timeLine.stop();
        }

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(delayMillis),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Plays a visual "bounce" animation on the game board container when a hard drop occurs.
     */
    public void playHardDropBounce() {
        if (gameBoard == null) return;


        javafx.scene.Node target = gameBoard.getParent();
        if (target == null) return;

        if (hardDropBounce != null) {
            hardDropBounce.stop();
        }
        target.setTranslateY(0);

        TranslateTransition impact = new TranslateTransition(Duration.millis(45), target);
        impact.setByY(1.5);
        TranslateTransition recover = new TranslateTransition(Duration.millis(70), target);
        recover.setByY(-1.5);

        hardDropBounce = new SequentialTransition(impact, recover);
        hardDropBounce.setCycleCount(1);
        hardDropBounce.playFromStart();
    }

    @FXML
    public void startAdventureGame(ActionEvent e) {
        startGame(GameMode.ADVENTURE);
    }

    @FXML
    public void startZenGame(ActionEvent e) {
        startGame(GameMode.ZEN);
    }

    @FXML
    public void resumeGame(ActionEvent event) {
        // Toggle pause back to playing
        if (gameController != null) {
            gameController.togglePause();
        }
        gamePanel.requestFocus();
    }

    @FXML
    public void restartGame(ActionEvent event) {
        pauseMenu.setVisible(false);
        gameController.createNewGame();
        gamePanel.requestFocus();
    }

    /**
     * FXML handler to return to the Main Menu scene.
     *
     * @param event The ActionEvent.
     */
    @FXML
    public void backToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            Stage stage = (Stage) pauseMenu.getScene().getWindow();
            controller.setStage(stage);
            stage.setScene(new Scene(root, 650, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * FXML handler to quit the application.
     *
     * @param event The ActionEvent.
     */
    @FXML
    public void quitGame(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Explicitly requests focus for the game panel to ensure keyboard input works.
     */
    public void focusGamePanel() {
        if (gamePanel != null) {
            gamePanel.requestFocus();
        }
    }

    /**
     * Opens the "How to Play" overlay from within the pause menu.
     *
     * @param event The ActionEvent.
     */
    @FXML
    private void openHowToFromPause(ActionEvent event) {
        if (howToOverlayInGame == null) return;

        howToOverlayInGame.setVisible(true);
        howToOverlayInGame.setMouseTransparent(false);

        FadeTransition ft = new FadeTransition(Duration.millis(200), howToOverlayInGame);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        SoundManager.getInstance().playClick();
    }

    /**
     * Closes the "How to Play" overlay and returns to the pause menu.
     *
     * @param event The ActionEvent.
     */
    @FXML
    private void closeHowToFromPause(ActionEvent event) {
        if (howToOverlayInGame == null) return;

        FadeTransition ft = new FadeTransition(Duration.millis(200), howToOverlayInGame);
        ft.setFromValue(howToOverlayInGame.getOpacity());
        ft.setToValue(0.0);
        ft.setOnFinished(e -> {
            howToOverlayInGame.setVisible(false);
            howToOverlayInGame.setMouseTransparent(true);
        });
        ft.play();

        SoundManager.getInstance().playClick();
    }

}
