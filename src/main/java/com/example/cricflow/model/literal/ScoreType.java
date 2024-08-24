package com.example.cricflow.model.literal;

public enum ScoreType {
    NO_RUN(0),
    SINGLE(1),
    DOUBLE(2),
    TRIPLE(3),
    FOUR(4),
    SIX(6);

    ScoreType(int score){
        this.score = score;
    }
    private final int score;
    public int getScore() {
        return score;
    }
}
