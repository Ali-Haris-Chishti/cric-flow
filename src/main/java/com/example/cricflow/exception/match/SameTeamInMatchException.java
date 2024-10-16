package com.example.cricflow.exception.match;

public class SameTeamInMatchException extends RuntimeException {
    public SameTeamInMatchException(long teamId) {
        super("Could Not Create Match: Both teams are same with ID: " + teamId);
    }
}
