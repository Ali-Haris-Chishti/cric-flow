package com.example.cricflow.exception.match;

public class TeamPlayerCountException extends RuntimeException {
    public TeamPlayerCountException(long teamAId, int aPlayers, long teamBId, int bPlayers) {
        super("Unable To create match: Both teams have uneven distribution of players, " +
                "team with ID: " + teamAId + " has '" + aPlayers + "', while " +
                "team with ID: " + teamBId + " has '" + bPlayers + "'");
    }
}
