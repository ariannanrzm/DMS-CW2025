package com.comp2042.ui;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import java.io.IOException;

public class NotificationPanel extends BorderPane {

    @FXML
    private Label notificationLabel;

    public NotificationPanel(String text) {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/NotificationPanel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        // Set the dynamic text passed from the game
        if (notificationLabel != null) {
            notificationLabel.setText(text);
        }
    }

    public void setStyleClass(String className) {
        if (notificationLabel != null) {
            notificationLabel.getStyleClass().remove("sideNotification");
            notificationLabel.getStyleClass().add(className);
        }
    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        ft.setFromValue(1);
        ft.setToValue(0);

        ft.setOnFinished((ActionEvent event) -> {
            list.remove(NotificationPanel.this);
        });
        ft.play();
    }
}
