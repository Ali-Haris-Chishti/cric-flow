package com.example.cricflow.model;

import com.example.cricflow.model.literal.TeamSide;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "MATCH_SEQUENCE",
            sequenceName = "MATCH_SEQ",
            allocationSize = 1
    )
    private Long matchId;

    @Column
    LocalDate matchDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ground_id")
    Ground ground;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_a_id")
    private Team teamA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_b_id")
    private Team teamB;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "toss_id")
    @JsonIgnore
    private Toss toss;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_innings_id")
    private Inning firstInnings;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "second_innings_id")
    private Inning secondInnings;

    @Column
    private int noOfOvers;

    @Column
    TeamSide winner;



    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean matchEquals(Match other) {
        if (this == other) return true;
        if (other == null) return false;

        System.out.println(this);

        return matchDate.equals(other.matchDate)
                && ground.groundEquals(other.ground)
                && teamA.teamEquals(other.teamA)
                && teamB.teamEquals(other.teamB)
                && toss.tossEquals(other.toss)
                && firstInnings.inningEquals(other.firstInnings)
                && secondInnings.inningEquals(other.secondInnings)
                && noOfOvers == other.noOfOvers
                && winner == other.winner;
    }


}
