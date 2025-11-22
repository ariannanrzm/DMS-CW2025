package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * NotificationPanel displays temporary score animations such as bonus points.
 * A fade and upward translation animation is applied before the panel removes
 * itself from the notification container.
 */
public class NotificationPanel extends BorderPane {

    private static final int PANEL_MIN_HEIGHT = 200;
    private static final int PANEL_MIN_WIDTH = 220;
    private static final double GLOW_LEVEL = 0.6;
    private static final int FADE_DURATION_MS = 2000;
    private static final int TRANSLATE_DURATION_MS = 2500;
    private static final int TRANSLATE_OFFSET_Y = 40;

    /**
     * Creates a notification panel displaying a styled score label.
     *
     * @param text the bonus text (e.g., "+100")
     */
    public NotificationPanel(String text) {
        setMinHeight(PANEL_MIN_HEIGHT);
        setMinWidth(PANEL_MIN_WIDTH);

        Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");

        Effect glow = new Glow(GLOW_LEVEL);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);

        setCenter(score);
    }

    /**
     * Plays the fade + slide animation and removes the panel from the parent list
     * when the animation completes.
     *
     * @param list the UI container holding this notification
     */
    public void showScore(ObservableList<Node> list) {

        FadeTransition fade = new FadeTransition(Duration.millis(FADE_DURATION_MS), this);
        fade.setFromValue(1);
        fade.setToValue(0);

        TranslateTransition move = new TranslateTransition(Duration.millis(TRANSLATE_DURATION_MS), this);
        move.setToY(getLayoutY() - TRANSLATE_OFFSET_Y);

        ParallelTransition animation = new ParallelTransition(move, fade);

        animation.setOnFinished(event -> list.remove(this));

        animation.play();
    }
}
