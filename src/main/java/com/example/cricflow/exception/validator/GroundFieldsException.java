package com.example.cricflow.exception.validator;

import java.util.List;

public class GroundFieldsException extends FieldValidatorException{
    public GroundFieldsException(List<String> violations) {
        super("Unable To create ground due to following violations: " + violations);
        this.violations = violations;
    }
}
