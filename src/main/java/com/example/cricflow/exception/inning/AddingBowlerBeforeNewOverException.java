package com.example.cricflow.exception.inning;

public class AddingBowlerBeforeNewOverException extends RuntimeException {
    public AddingBowlerBeforeNewOverException(long id) {
        super("Trying to add a bowler with ID: " + id + ", for new over which hasn't started yet");
    }
}
