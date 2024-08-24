package com.example.cricflow.exception;

public class EntityDoesNotExists extends RuntimeException {
    public EntityDoesNotExists(Long id) {
        super("Entity with ID: " + id + " does not exist");
    }
}
