package com.example.cricflow.exception.validator;

import java.util.List;

public class FieldValidatorException extends RuntimeException {
    public List<String> violations;
    public FieldValidatorException(String message) {
        super(message);
    }
}
