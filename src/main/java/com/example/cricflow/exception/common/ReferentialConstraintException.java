package com.example.cricflow.exception.common;

public class ReferentialConstraintException extends RuntimeException {
    public ReferentialConstraintException(String referencing, String referenced, long id) {
        super("Can not delete " + referencing + " object with id " + id + " as it is referencing " + referenced + " object," +
                " if you want to delete, you need to delete referenced " + referenced + "(s) first");
    }
}
