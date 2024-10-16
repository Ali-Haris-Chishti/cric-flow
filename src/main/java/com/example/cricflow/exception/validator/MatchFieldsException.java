package com.example.cricflow.exception.validator;

import java.util.List;

public class MatchFieldsException extends FieldValidatorException{

    public MatchFieldsException(List<String> violations) {
        super("Unable To create match due to following violations: " + violations);
        this.violations = violations;
    }

}
