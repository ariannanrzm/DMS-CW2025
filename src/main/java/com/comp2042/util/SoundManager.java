package com.comp2042.util;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;
import javafx.scene.Parent;
import java.net.URL;
import java.util.Set;

/**
 * SoundManager handles loading and playing UI sound effects.
 * Implements the Singleton pattern to ensure resources are loaded only once.
 */
public class SoundManager {

    private static SoundManager instance;
    private final AudioClip clickSound;
    private final AudioClip hoverSound;
    private final AudioClip bonusSound;

    private SoundManager() {
        // Load resources safely
        clickSound = loadSound("click.wav");
        hoverSound = loadSound("hover.wav");
        bonusSound = loadSound("bonus.mp3");
    }

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private AudioClip loadSound(String filename) {
        URL resource = getClass().getResource("/sounds/" + filename);
        if (resource == null) {
            System.err.println("Sound file missing: " + filename);
            return null;
        }
        return new AudioClip(resource.toExternalForm());
    }

    public void playClick() {
        if (clickSound != null) clickSound.play();
    }

    public void playHover() {
        if (hoverSound != null) hoverSound.play();
    }

    public void playBonus() {
        if (bonusSound != null) {
            bonusSound.play();
        }
    }
    /**
     * Utility method to auto-register sounds to all buttons in a container.
     * This avoids code duplication in controllers.
     */
    public void registerButtons(Parent root) {
        // Find all nodes with the style class "button"
        Set<Node> buttons = root.lookupAll(".button");

        for (Node node : buttons) {
            if (node instanceof Button button) {
                // Attach Hover Sound
                button.setOnMouseEntered(event -> playHover());

                // Attach Click Sound
                button.addEventHandler(javafx.event.ActionEvent.ACTION, event -> playClick());
            }
        }
    }
}