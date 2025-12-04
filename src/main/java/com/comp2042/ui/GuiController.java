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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GuiController manages visual updates and animations.
 */
public class GuiController implements Initializable {

    private static final int BOARD_BORDER_OFFSET = 2;
    private final Paint[] paintCache = new Paint[8];
    private final Border BLOCK_BORDER = new Border(new BorderStroke(
            Color.web("rgba(255,255,255,0.4)"), Color.TRANSPARENT, Color.TRANSPARENT, Color.web("rgba(255,255,255,0.4)"),
            BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY
    ));

    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private GameOverPanel gameOverPanel;
    @FXML private Label scoreLabel;
    @FXML private Label linesLabel;
    @FXML private VBox nextBrickContainer;
    @FXML private VBox holdBrickContainer;
    @FXML private Label levelLabel;


    private StackPane[][] displayMatrix;
    private StackPane[][] rectangles;
    private InputEventListener eventListener;
    private Timeline timeLine;
    private InputHandler inputHandler;
    private GameController gameController;
    private GridPane ghostPanel;

    private StackPane[][] ghostRectangles;
    private List<int[][]> lastNextBricks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.getStyleClass().add("game-grid");
        brickPanel.toFront();
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gameOverPanel.setVisible(false);

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

    }


    public void setGameController(GameController controller) {
        this.gameController = controller;
        this.inputHandler = new InputHandler(controller);
        inputHandler.attachTo(gamePanel);
    }

    public void startGame(GameMode mode) {
        // Stop any existing game loop first
        if (timeLine != null) {
            timeLine.stop();
            timeLine = null;
        }

        new GameController(this, mode);
        // Focus the game panel so keyboard controls work immediately
        gamePanel.requestFocus();
    }


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
        updateBrickPanelPosition(brick);
        refreshNextBricks(brick.getNextBricks());
        refreshHeldBrick(brick.getHeldBrickData());

        updateGameSpeed(initialSpeed);
    }


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

    private void showClearRowNotication(ClearRow clearRow){
    }

    public void showLevelUpNotification(int newLevel) {
        NotificationPanel notification = new NotificationPanel("LEVEL " + newLevel);
        groupNotification.getChildren().add(notification);
        notification.showScore(groupNotification.getChildren());
    }

    public void showNotificationIfRowsCleared(ClearRow clearRow) {
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
            // Delay the combo notification slightly or stack it
            NotificationPanel comboNotification = new NotificationPanel("COMBO x" + currentCombo);
            comboNotification.setTranslateY(30);
            groupNotification.getChildren().add(comboNotification);
            comboNotification.showScore(groupNotification.getChildren());
        }
    }

    private void updateBrickPanelPosition(ViewData brick) {
        int BRICK_SIZE = GameConfig.get().getBrickSize();
        int TOP_OFFSET = GameConfig.get().getTopOffset();

        // Correct the visual offset when brick falls
        final int CELL_STEP = BRICK_SIZE + 1;

        brickPanel.setLayoutX(gamePanel.getLayoutX()
                + BOARD_BORDER_OFFSET
                + brick.getxPosition() * CELL_STEP
        );

        brickPanel.setLayoutY(TOP_OFFSET + gamePanel.getLayoutY()
                + brick.getyPosition() * CELL_STEP);

        if (ghostPanel != null) {
            ghostPanel.setLayoutX(gamePanel.getLayoutX() + BOARD_BORDER_OFFSET + brick.getxPosition() * CELL_STEP);
            ghostPanel.setLayoutY(TOP_OFFSET + gamePanel.getLayoutY() + brick.getGhostYPosition() * CELL_STEP);
        }
    }

    private Paint getFillColor(int i) {
        if (i >= 0 && i < paintCache.length) {
            return paintCache[i];
        }
        return Color.TRANSPARENT;
    }

    private void setRectangleData(int color, StackPane r) {
        Paint fill = getFillColor(color);
        r.setBackground(new Background(new BackgroundFill(fill, CornerRadii.EMPTY, Insets.EMPTY)));
        if (color == 0) {
            r.setBorder(Border.EMPTY);
        } else {
            r.setBorder(BLOCK_BORDER);
        }    }

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

    private void refreshHeldBrick(int[][] matrix) {
        holdBrickContainer.getChildren().clear();
        Label title = new Label("HOLD");
        title.getStyleClass().add("scoreTitle");
        holdBrickContainer.getChildren().add(title);

        if (matrix == null) return;

        int PREVIEW_SIZE = 15;
        GridPane previewPane = new GridPane();
        previewPane.setAlignment(Pos.CENTER);
        previewPane.setHgap(1);
        previewPane.setVgap(1);

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] != 0) {
                    StackPane r = new StackPane();
                    r.setPrefSize(PREVIEW_SIZE, PREVIEW_SIZE);
                    r.getStyleClass().add("game-block");
                    r.setBackground(new Background(new BackgroundFill(getFillColor(matrix[row][col]), CornerRadii.EMPTY, Insets.EMPTY)));

                    previewPane.add(r, col, row);
                }
            }
        }
        holdBrickContainer.getChildren().add(previewPane);
    }

    private void refreshNextBricks(List<int[][]> nextBricks) {
        nextBrickContainer.getChildren().clear();
        Label title = new Label("NEXT");
        title.getStyleClass().add("scoreTitle");
        nextBrickContainer.getChildren().add(title);

        int PREVIEW_SIZE = 15;

        for (int[][] matrix : nextBricks) {
            GridPane previewPane = new GridPane();
            previewPane.setAlignment(Pos.CENTER);
            previewPane.setHgap(1);
            previewPane.setVgap(1);

            for (int row = 0; row < matrix.length; row++) {
                for (int col = 0; col < matrix[row].length; col++) {
                    if (matrix[row][col] != 0) {
                        StackPane r = new StackPane();
                        r.setPrefSize(PREVIEW_SIZE, PREVIEW_SIZE);
                        r.getStyleClass().add("game-block");

                        r.setBackground(new Background(new BackgroundFill(getFillColor(matrix[row][col]), CornerRadii.EMPTY, Insets.EMPTY)));

                        previewPane.add(r, col, row);
                    }
                }
            }
            nextBrickContainer.getChildren().add(previewPane);
        }
    }

    public void refreshGameBackground(int[][] board) {
        int HIDDEN_ROWS = GameConfig.get().getHiddenRows();

        for (int i = HIDDEN_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

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

    public void setEventListener(InputEventListener listener) {
        this.eventListener = listener;
    }

    public void bindScore(IntegerProperty property) {
        scoreLabel.textProperty().bind(property.asString("%d"));
    }

    public void bindLines(IntegerProperty property) {
        linesLabel.textProperty().bind(property.asString("%d"));
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        brickPanel.setVisible(false);
        if (ghostPanel != null) {
            ghostPanel.setVisible(false);
        }
    }

    public void resetTimeline() {
        if (timeLine != null) {
            timeLine.stop();
            timeLine.play();
        }
    }

    /**
     * Resets the view for a new game.
     */
    public void resetGameView() {
        gameOverPanel.setVisible(false);
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

    public void showPauseMessage(boolean isPaused) {
        if (isPaused) {
            NotificationPanel notif = new NotificationPanel("PAUSED");
            groupNotification.getChildren().add(notif);
            notif.showScore(groupNotification.getChildren());
            timeLine.pause();
        } else {
            timeLine.play();
        }
    }

    public void bindLevel(IntegerProperty property) {
        levelLabel.textProperty().bind(property.asString("%d"));
    }

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

    @FXML
    public void startAdventureGame(ActionEvent e) {
        startGame(GameMode.ADVENTURE);
    }

    @FXML
    public void startZenGame(ActionEvent e) {
        startGame(GameMode.ZEN);
    }


}
