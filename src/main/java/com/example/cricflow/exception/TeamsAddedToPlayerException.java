package com.example.cricflow.exception;

public class TeamsAddedToPlayerException extends RuntimeException {
    public TeamsAddedToPlayerException() {
        super("Players can be added to the team, but team can not be assigned to players, due to cyclic behaviour");
    }
}
