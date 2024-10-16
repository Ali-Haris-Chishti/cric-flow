package com.example.cricflow.exception.common;

public class NameAlreadyExistsException extends RuntimeException {
    public NameAlreadyExistsException(String object, String name) {
        super("'" + object+  "' with name: '" + name + "' already exists, " + object+ " name must be unique");
    }
}
