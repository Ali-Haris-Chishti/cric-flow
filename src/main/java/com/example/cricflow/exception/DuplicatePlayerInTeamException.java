package com.example.cricflow.exception;

public class DuplicatePlayerInTeamException extends RuntimeException {
    public DuplicatePlayerInTeamException(Long playerId, Long ParentId) {
        super("Player with ID: " + playerId + " already exists in team with ID: " + ParentId);
    }
}
