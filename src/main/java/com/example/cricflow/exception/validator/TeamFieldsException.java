package com.example.cricflow.exception.validator;

import java.util.List;

public class TeamFieldsException extends FieldValidatorException{
    public TeamFieldsException(List<String> violations) {
        super("Unable To create team due to following violations: " + violations);
        this.violations = violations;
    }
}
