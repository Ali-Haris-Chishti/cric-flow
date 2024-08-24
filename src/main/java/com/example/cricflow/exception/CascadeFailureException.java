package com.example.cricflow.exception;

public class CascadeFailureException extends RuntimeException {
    public CascadeFailureException(String parent, String child, String operation) {
        super("Could not '" + operation + "', '" + child + "' through '" + parent + "', due to insufficient authority");
    }
}
