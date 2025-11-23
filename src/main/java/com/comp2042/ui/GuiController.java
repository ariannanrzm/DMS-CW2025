package com.comp2042.ui;

import com.comp2042.game.board.DownData;
import com.comp2042.game.board.ViewData;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * GuiController manages visual updates, keyboard input, and animations for
 * the Tetris game. This refactored version replaces magic numbers and extracts
 * repeated UI update logic for improved readability.
 */
public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int TOP_OFFSET = -42;
    private static final int ARC_RADIUS = 9;
    private static final int DROP_INTERVAL_MS = 400;
    private static final int HIDDEN_ROWS = 2;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;

    private InputEventListener eventListener;
    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                if (!isPause.get() && !isGameOver.get()) {

                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                    }

                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                    }

                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                    }

                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                    }
                }

                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }

                keyEvent.consume();
            }
        });

        gameOverPanel.setVisible(false);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];

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
                Duration.millis(DROP_INTERVAL_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private void updateBrickPanelPosition(ViewData brick) {
        brickPanel.setLayoutX(gamePanel.getLayoutX()
                + brick.getxPosition() * brickPanel.getVgap()
                + brick.getxPosition() * BRICK_SIZE);

        brickPanel.setLayoutY(TOP_OFFSET + gamePanel.getLayoutY()
                + brick.getyPosition() * brickPanel.getHgap()
                + brick.getyPosition() * BRICK_SIZE);
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
        r.setArcHeight(ARC_RADIUS);
        r.setArcWidth(ARC_RADIUS);
    }

    private void refreshBrick(ViewData brick) {
        if (isPause.get()) return;
        updateBrickPanelPosition(brick);

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = HIDDEN_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);

            // 1) If rows were cleared, show notification
            if (downData.getClearRow() != null &&
                    downData.getClearRow().getLinesRemoved() > 0) {

                // You no longer have scoreBonus in ClearRow, so keep it simple:
                NotificationPanel notif =
                        new NotificationPanel("Lines cleared: " + downData.getClearRow().getLinesRemoved());

                groupNotification.getChildren().add(notif);
                notif.showScore(groupNotification.getChildren());
            }

            // 2) Always refresh the background from the board matrix
            refreshGameBackground(downData.getBoardMatrix());

            // 3) Refresh the falling brick
            refreshBrick(downData.getViewData());

            // 4) If the controller says game over, show it
            if (downData.isGameOver()) {
                gameOver();
            }
        }
        gamePanel.requestFocus();
    }



    public void setEventListener(InputEventListener listener) {
        this.eventListener = listener;
    }

    public void bindScore(IntegerProperty property) {}

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.set(true);
    }

    public void newGame(ActionEvent e) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.set(false);
        isGameOver.set(false);
    }

    public void pauseGame(ActionEvent e) {
        gamePanel.requestFocus();
    }
}
