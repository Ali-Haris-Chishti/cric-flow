package com.example.cricflow.exception;

public class GroundNameAlreadyExistsException extends RuntimeException {
    public GroundNameAlreadyExistsException(String groundName) {
        super("Ground name " + groundName + " already exists, Ground name must be unique");
    }
}
