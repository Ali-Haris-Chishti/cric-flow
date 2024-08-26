package com.example.cricflow.exception.validator;

import java.util.List;

public class PlayerFieldsException extends FieldValidatorException{
    public PlayerFieldsException(List<String> violations) {
        super("Unable To create player due to following violations: " + violations);
        this.violations = violations;
    }
}