package com.example.cricflow;

import com.example.cricflow.model.*;
import com.example.cricflow.model.BallEvent;
import com.example.cricflow.model.event.Extra;
import com.example.cricflow.model.event.Score;
import com.example.cricflow.model.event.Wicket;
import com.example.cricflow.model.literal.ExtraType;
import com.example.cricflow.model.literal.ScoreType;
import com.example.cricflow.model.literal.TeamSide;
import com.example.cricflow.model.literal.WicketType;

import java.time.LocalDate;

public abstract class BaseData {
    protected static Ground ground1, ground2;
    protected static Player player1, player2, player3, player4;
    protected static Match match1, match2;
    protected static Team teamA, teamB;
    protected static Toss toss1, toss2;
    protected static Inning inning1, inning2, inning3, inning4;
    protected static Over over1, over2;
    protected static Ball ball1, ball2, ball3, ball4;
    protected static BallEvent event1, event2, event3, event4;

    public abstract void deleteRelatedTablesData();
    public abstract void prepare();

    static {

        ground1 = Ground.builder()
                .groundName("SCME Ground")
                .build();
        ground2 = Ground.builder()
                .groundName("NICE Ground")
                .build();

        player1 = Player.builder()
                .firstName("Ali")
                .lastName("Haris")
                .battingStyle(Player.BattingStyle.RIGHT_HANDED)
                .playerType(Player.PlayerType.BATSMAN)
                .build();
        player2 = Player.builder()
                .firstName("Tauha")
                .lastName("Kashif")
                .battingStyle(Player.BattingStyle.RIGHT_HANDED)
                .playerType(Player.PlayerType.BOWLER)
                .build();
        player3 = Player.builder()
                .firstName("Ali")
                .lastName("Hamza")
                .battingStyle(Player.BattingStyle.LEFT_HANDED)
                .playerType(Player.PlayerType.ALL_ROUNDER)
                .build();
        player4 = Player.builder()
                .firstName("Abdul")
                .lastName("Sami")
                .battingStyle(Player.BattingStyle.RIGHT_HANDED)
                .playerType(Player.PlayerType.BATSMAN)
                .build();

        teamA = Team.builder()
                .teamName("Lahore Qalandars")
                .build();
        teamB = Team.builder()
                .teamName("Karachi Kings")
                .build();

        match1 = Match.builder()
                .matchDate(LocalDate.of(2024, 8, 28))
                .noOfOvers(5)
                .winner(TeamSide.SIDE_A)
                .build();
        match2 = Match.builder()
                .matchDate(LocalDate.of(2024, 8, 31))
                .noOfOvers(7)
                .winner(TeamSide.SIDE_B)
                .build();

        toss1 = Toss.builder()
                .winningSide(TeamSide.SIDE_A)
                .battingSide(TeamSide.SIDE_A)
                .bowlingSide(TeamSide.SIDE_B)
                .build();

        toss2 = Toss.builder()
                .winningSide(TeamSide.SIDE_B)
                .battingSide(TeamSide.SIDE_A)
                .bowlingSide(TeamSide.SIDE_B)
                .build();

        event1 = Wicket.builder()
                .wicketType(WicketType.BOWLED)
                .build();
        event2 = Score.builder()
                .scoreType(ScoreType.SIX)
                .build();
        event3 = Score.builder()
                .scoreType(ScoreType.NO_RUN)
                .build();
        event4 = Extra.builder()
                .extraType(ExtraType.NO_BALL)
                .scoreType(ScoreType.DOUBLE)
                .build();


        ball1 = Ball.builder().build();
        ball2 = Ball.builder().build();
        ball3 = Ball.builder().build();
        ball4 = Ball.builder().build();

        over1 = Over.builder().build();
        over2 = Over.builder().build();

        inning1 = Inning.builder()
                .numberOfOvers(10)
                .build();
        inning2 = Inning.builder()
                .numberOfOvers(8)
                .build();
        inning3 = Inning.builder()
                .numberOfOvers(7)
                .build();
        inning4 = Inning.builder()
                .numberOfOvers(7)
                .build();
    }
}
