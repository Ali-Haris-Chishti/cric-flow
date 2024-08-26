package com.example.cricflow.exception;

public class EntityDoesNotExistsException extends RuntimeException {
    public EntityDoesNotExistsException(String entity, Long id) {
        super(entity.toUpperCase() + " with ID: " + id + " does not exist");
    }
}
