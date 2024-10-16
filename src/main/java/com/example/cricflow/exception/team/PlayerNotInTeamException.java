package com.example.cricflow.exception.team;

public class PlayerNotInTeamException extends RuntimeException {
    public PlayerNotInTeamException(long playerId, String role, String team) {
        super("Player with ID: " + playerId + " given as " + role + " is not in team " + team);
    }
}
