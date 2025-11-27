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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * GuiController manages visual updates and animations.
 */
public class GuiController implements Initializable {

    private static final int BOARD_BORDER_OFFSET = 2;

    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private InputEventListener eventListener;
    private Timeline timeLine;
    private InputHandler inputHandler;
    private GameController gameController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.getStyleClass().add("game-grid");
        brickPanel.toFront();
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gameOverPanel.setVisible(false);
    }


    public void setGameController(GameController controller) {
        this.gameController = controller;
        this.inputHandler = new InputHandler(controller);
        inputHandler.attachTo(gamePanel);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        // singleton used here
        int HIDDEN_ROWS = GameConfig.get().getHiddenRows();
        int BRICK_SIZE = GameConfig.get().getBrickSize();

        for (int i = HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = r;
                gamePanel.add(r, j, i - HIDDEN_ROWS);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(brick.getBrickData()[i][j], r);
                rectangles[i][j] = r;
                brickPanel.add(r, j, i);
            }
        }

        updateBrickPanelPosition(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(GameConfig.get().getDropInterval()),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }


    private void showClearRowNotication(ClearRow clearRow){
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            NotificationPanel notif = new NotificationPanel("Lines Cleared: " + clearRow.getLinesRemoved());
            groupNotification.getChildren().add(notif);
            notif.showScore(groupNotification.getChildren());
        }
    }

    public void showNotificationIfRowsCleared(ClearRow clearRow) {
        showClearRowNotication(clearRow);
    }

    private void updateBrickPanelPosition(ViewData brick) {
        int BRICK_SIZE = GameConfig.get().getBrickSize();
        int TOP_OFFSET = GameConfig.get().getTopOffset();

        // Correct the visual offset when brick falls.
        final int CELL_STEP = BRICK_SIZE + 1;
        final int RIGHT_EDGE_VISUAL_TWEAK = 1;
        brickPanel.setLayoutX(gamePanel.getLayoutX()
                + BOARD_BORDER_OFFSET
                + brick.getxPosition() * CELL_STEP
                - RIGHT_EDGE_VISUAL_TWEAK
        );

        brickPanel.setLayoutY(TOP_OFFSET + gamePanel.getLayoutY()
                + brick.getyPosition() * CELL_STEP);
    }

    private Paint getFillColor(int i) {
        switch (i) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.AQUA;
            case 2: return Color.BLUEVIOLET;
            case 3: return Color.DARKGREEN;
            case 4: return Color.YELLOW;
            case 5: return Color.RED;
            case 6: return Color.BEIGE;
            case 7: return Color.BURLYWOOD;
            default: return Color.WHITE;
        }
    }

    private void setRectangleData(int color, Rectangle r) {
        r.setFill(getFillColor(color));

        r.setArcHeight(GameConfig.get().getArcRadius());
        r.setArcWidth(GameConfig.get().getArcRadius());
    }

    public void refreshBrick(ViewData brick) {
        updateBrickPanelPosition(brick);
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
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

    public void bindScore(IntegerProperty property) {}

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        brickPanel.setVisible(false);
    }

    public void resetTimeline() {
        if (timeLine != null) {
            timeLine.stop();
            timeLine.play(); // Restarts the 400ms countdown from 0
        }
    }

    /**
     * Resets the view for a new game.
     */
    public void resetGameView() {
        gameOverPanel.setVisible(false);
        brickPanel.setVisible(true);
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

}
