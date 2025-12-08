package com.comp2042.game.events;

/**
 * Enumeration defining the origin of a game event.
 * Used to distinguish between direct user input and automated game loop actions.
 */
public enum EventSource {
    USER, THREAD
}
