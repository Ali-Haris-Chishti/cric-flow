package com.example.cricflow.exception.inning;

public class InningInitializationException extends RuntimeException {
    public InningInitializationException(long id, String status) {
        super("Could not initialize innings with id: " + id + " because innings is already: " + status);
    }

    public InningInitializationException(String message) {
        super(message);
    }
}
