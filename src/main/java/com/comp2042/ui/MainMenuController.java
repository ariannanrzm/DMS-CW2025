package com.comp2042.ui;

import com.comp2042.game.config.GameMode;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import com.comp2042.util.SoundManager;
import javafx.application.Platform;
import com.comp2042.util.MusicManager;


import java.net.URL;

public class MainMenuController {

    @FXML private StackPane rootPane;
    @FXML private Label titleLabel;

    private FadeTransition introFade;
    private Stage stage;

    @FXML public void initialize() {

        // Menu fade in
        introFade = new FadeTransition(Duration.seconds(1), rootPane);
        introFade.setFromValue(0);
        introFade.setToValue(1);

        // Title glow
        FadeTransition titlePulse = new FadeTransition(Duration.seconds(2), titleLabel);
        titlePulse.setFromValue(1.0);
        titlePulse.setToValue(0.75);
        titlePulse.setAutoReverse(true);
        titlePulse.setCycleCount(FadeTransition.INDEFINITE);
        titlePulse.play();

        // Register sounds for all buttons
        Platform.runLater(() -> SoundManager.getInstance().registerButtons(rootPane));
        MusicManager.getInstance().playMusic("menu.mp3", true);
    }

    /** Called whenever the menu is shown. */
    public void playIntroAnimation() {
        if (introFade == null) return;

        rootPane.setOpacity(0);
        introFade.stop();
        introFade.playFromStart();
    }

    /**
     * Called from Main to give this controller the primary stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void loadGame(GameMode mode) {
        try {
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();
            GuiController guiController = loader.getController();
            guiController.startGame(mode);

            Scene scene = new Scene(root, 650, 600);
            scene.getStylesheets()
                    .add(getClass().getResource("/window_style.css").toExternalForm());

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fadeOutAndLoad(GameMode mode) {
        if (rootPane == null) {
            loadGame(mode);
            return;
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);
        fadeOut.setOnFinished(e -> loadGame(mode));
        fadeOut.play();
    }

    @FXML
    private void startAdventure(ActionEvent event) {
        fadeOutAndLoad(GameMode.ADVENTURE);

    }

    @FXML
    private void startZen(ActionEvent event) {
        fadeOutAndLoad(GameMode.ZEN);
    }

}
