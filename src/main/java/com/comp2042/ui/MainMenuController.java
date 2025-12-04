package com.comp2042.ui;

import com.comp2042.game.config.GameMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainMenuController {

    private Stage stage;

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

    @FXML
    private void startClassic(ActionEvent event) {
        loadGame(GameMode.CLASSIC);
    }

    @FXML
    private void startZen(ActionEvent event) {
        loadGame(GameMode.ZEN);
    }

    @FXML
    private void startChaos(ActionEvent event) {
        loadGame(GameMode.CHAOS);
    }
}
