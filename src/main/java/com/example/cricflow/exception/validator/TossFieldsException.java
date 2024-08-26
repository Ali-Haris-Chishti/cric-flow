package com.example.cricflow.exception.validator;

import java.util.List;

public class TossFieldsException extends FieldValidatorException {

    public TossFieldsException(List<String> violations) {
        super("Unable To create toss due to following violations: " + violations);
        this.violations = violations;
    }
}
