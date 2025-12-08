package com.comp2042;

import com.comp2042.ui.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The entry point for the Tetris application.
 * This class extends JavaFX Application and is responsible for setting up the primary stage,
 * loading the main menu scene, and launching the application.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application by loading the Main Menu FXML and displaying the primary stage.
     *
     * @param primaryStage The primary window for this application, onto which the application scene can be set.
     * @throws Exception If the FXML resource cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("MainMenu.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();

        MainMenuController controller = fxmlLoader.getController();
        controller.setStage(primaryStage);
        controller.playIntroAnimation();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 650, 600);
        scene.getStylesheets().add(getClass().getResource("/window_style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        controller.playIntroAnimation();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
