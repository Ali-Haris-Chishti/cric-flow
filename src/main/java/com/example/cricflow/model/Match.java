package com.example.cricflow.model;

import com.example.cricflow.model.literal.TeamSide;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

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
    @NotNull(message = "matchDate can not be null")
    LocalDate matchDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ground_id")
    @NotNull(message = "ground can not be null")
    Ground ground;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_a_id")
    @NotNull(message = "teamA can not be null")
    private Team teamA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_b_id")
    @NotNull(message = "teamB can not be null")
    private Team teamB;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "toss_id")
    @NotNull(message = "toss can not be null")
    private Toss toss;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "first_innings_id")
    @JsonIgnore
    private Inning firstInnings;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "second_innings_id")
    @JsonIgnore
    private Inning secondInnings;

    @Column()
    @NotNull(message = "no Of Overs can not be null")
    @Min(value = 2, message = "no of overs must be at least 2")
    @Max(value = 20, message = "no of overs must be at most 20")
    private Integer noOfOvers;

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
                && Objects.equals(noOfOvers, other.noOfOvers)
                && winner == other.winner;
    }


}
