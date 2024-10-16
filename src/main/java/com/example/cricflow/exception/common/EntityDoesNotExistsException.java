package com.example.cricflow.exception.common;

public class EntityDoesNotExistsException extends RuntimeException {

    public EntityDoesNotExistsException(String entity, Long id) {
        super(entity.toUpperCase() + " with ID: " + id + " does not exist");
    }

    public EntityDoesNotExistsException(String entity, String name) {
        super(entity.toUpperCase() + " with Name: " + name + " does not exist");
    }
}
