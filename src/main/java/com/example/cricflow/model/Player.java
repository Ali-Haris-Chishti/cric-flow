package com.example.cricflow.model;

import com.example.cricflow.model.literal.ExcludedFromToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.lang.reflect.Field;
import java.util.List;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

//@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "PLAYER_SEQUENCE",
            sequenceName = "PLAYER_SEQ",
            allocationSize = 1
    )
    private Long playerId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    @Enumerated(EnumType.STRING)
    private PlayerType playerType;

    @Column
    @Enumerated(EnumType.STRING)
    private BattingStyle battingStyle;


    @ManyToMany(
            targetEntity = Team.class,
            mappedBy = "players",
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    @ExcludedFromToString
    @Setter(AccessLevel.NONE)
    List<Team> teams;

    static public enum PlayerType {
        BATSMAN,
        BOWLER,
        ALL_ROUNDER
    }

    static public enum BattingStyle {
        LEFT_HANDED,
        RIGHT_HANDED
    }

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean playerEquals(Player other) {
        if (this == other) return true;
        if (other == null) return false;

        System.out.println(this);

        return firstName.equals(other.firstName)
                && lastName.equals(other.lastName)
                && playerType == other.playerType
                && battingStyle == other.battingStyle;
    }


}
