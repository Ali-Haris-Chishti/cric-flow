package com.example.cricflow.exception;

public class OverExceedsFixedNumber extends RuntimeException {
    public OverExceedsFixedNumber(int overs, long inningsId) {
        super("Can not add more overs than " + overs + " for this innings with id: " + inningsId);
    }
}
