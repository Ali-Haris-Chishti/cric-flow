package com.example.cricflow.exception.inning;

public class OverFinishedException extends RuntimeException{
    public OverFinishedException() {
        super("Over finished, before adding ball initialize over with bowler first");
    }
}
