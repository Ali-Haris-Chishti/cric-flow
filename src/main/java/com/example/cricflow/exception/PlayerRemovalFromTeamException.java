package com.example.cricflow.exception;

public class PlayerRemovalFromTeamException extends RuntimeException {
    public PlayerRemovalFromTeamException(Long teamId, Long PlayerId) {
        super("Requested to remove player with id: " + PlayerId + " from team: " + teamId +
                ", which does not exists in the team");
    }
}
