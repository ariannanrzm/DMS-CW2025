package com.comp2042.game.events;


/**
 * Represents a movement-related input event in the Tetris game.
 * MoveEvent encapsulates the type of movement requested
 * and the source that triggered it.
 *
 * This class is immutable and used for communication between
 * the GUI controller and the game logic (GameController).
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent containing a movement type and its source.
     *
     * @param eventType  the type of movement being requested
     * @param eventSource the origin of the input event
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}
