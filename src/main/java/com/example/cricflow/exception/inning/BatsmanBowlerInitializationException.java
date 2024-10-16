package com.example.cricflow.exception.inning;

public class BatsmanBowlerInitializationException extends RuntimeException {
    public BatsmanBowlerInitializationException(long playerId, String field1, String field2) {
        super("Id's for " + field1 + " and " + field2 + " are same: " + playerId);
    }
}
