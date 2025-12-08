package com.comp2042.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * MusicManager handles background music for the game.
 * It ensures music loops continuously and allows smooth transitions between tracks.
 */
public class MusicManager {

    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private String currentTrackName = "";

    private MusicManager() {}

    public static synchronized MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    /**
     * Plays a music file in a loop.
     *
     * @param filename
     * @param shouldLoop TRUE for background music, FALSE for one-shot tracks (Win/GameOver)
     *
     */
    public void playMusic(String filename, boolean shouldLoop) {
        // Prevent restarting the same song if it's already playing
        if (currentTrackName.equals(filename) && mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        stopMusic();

        try {
            URL resource = getClass().getResource("/sounds/" + filename);
            if (resource == null) {
                System.err.println("Music file not found: " + filename);
                return;
            }

            Media media = new Media(resource.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            // Logic for looping
            if (shouldLoop) {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
            } else {
                mediaPlayer.setCycleCount(1); // Play once
            }

            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();

            currentTrackName = filename;

        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Release resources
            mediaPlayer = null;
            currentTrackName = "";
        }
    }
}