package aoop.asteroids.model;

/**
 * This is an enumeration of the possible messages sent by the Joiner to the JoinerServer.
 */

public enum Messages {
    CONNECT,
    DISCONNECT,
    UP,
    LEFT,
    RIGHT,
    SPACE,
    STOP_UP,
    STOP_LEFT,
    STOP_RIGHT,
    STOP_SPACE;
}