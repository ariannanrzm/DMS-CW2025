package com.comp2042.ui;

import com.comp2042.game.config.GameMode;
import com.comp2042.game.logic.HighScoreManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class GameOverController {

    @FXML private Label scoreLabel;
    @FXML private Label titleLabel;
    @FXML private Label highScoreLabel;
    @FXML private Label bestTimeLabel;
    @FXML private Label timeLabel;


    /**
     * Sets the score text.
     */
    public void setScore(int score) {
        scoreLabel.setText("SCORE: " + score);
    }

    public void setTitle(String text, javafx.scene.paint.Paint color) {
        if (titleLabel != null) {
            titleLabel.setText(text);
            titleLabel.setTextFill(color);
        }
    }

    public void setGameTime(int seconds) {
        if (timeLabel != null) {
            int m = seconds / 60;
            int s = seconds % 60;
            timeLabel.setText(String.format("TIME: %02d:%02d", m, s));
        }
    }

    public void showRecords() {
        if (highScoreLabel == null || bestTimeLabel == null) return;

        int bestScore = HighScoreManager.getHighScore();
        int bestTime = HighScoreManager.getFastestTime();

        String timeStr = (bestTime == Integer.MAX_VALUE) ? "--:--" : formatTime(bestTime);

        highScoreLabel.setText("HIGH SCORE: " + bestScore);
        bestTimeLabel.setText("BEST TIME: " + timeStr);
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Smoothly fades in the score label when the scene opens.
     */
    public void animateEntry() {
        showRecords();

        if (scoreLabel != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(1000), scoreLabel);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }

        if (timeLabel != null) {
            FadeTransition ftTime = new FadeTransition(Duration.millis(1000), timeLabel);
            ftTime.setFromValue(0.0);
            ftTime.setToValue(1.0);
            ftTime.play();
        }
    }

    @FXML
    public void retryGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent root = loader.load();
            GuiController guiController = loader.getController();

            guiController.startGame(GameMode.ADVENTURE);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            guiController.focusGamePanel();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void quitGame(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void initialize() {
        if (scoreLabel != null) {
            scoreLabel.setOpacity(0);
        }
    }
}