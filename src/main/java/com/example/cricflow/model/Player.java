package com.example.cricflow.model;

import com.example.cricflow.model.literal.ExcludedFromToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.lang.reflect.Field;
import java.util.List;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

//@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @NotNull(message = "firstName can not be null")
    @Size(min = 2, max = 20, message = "firstName must be 2-20 characters long")
    private String firstName;

    @Column
    @NotNull(message = "lastName can not be null")
    @Size(min = 2, max = 20, message = "lastName must be 2-20 characters long")
    private String lastName;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "playerType can not be null")
    private PlayerType playerType;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "battingStyle can not be null")
    private BattingStyle battingStyle;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "bowlingStyle can not be null")
    private BowlingStyle bowlingStyle;


    @ManyToOne(
            targetEntity = Team.class,
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    @ExcludedFromToString
    @JoinColumn(name = "team_id")
    Team team;

    static public enum PlayerType {
        BATSMAN,
        BOWLER,
        ALL_ROUNDER
    }

    static public enum BattingStyle {
        LEFT_HANDED,
        RIGHT_HANDED
    }

    static public enum BowlingStyle {
        LEFT_ARM_FAST_BOWLER,
        RIGHT_ARM_FAST_BOWLER,
        LEFT_ARM_OFF_SPINNER,
        RIGHT_ARM_OFF_SPINNER,
        LEFT_ARM_CHINA_MAN,
        RIGHT_ARM_LEG_SPINNER,
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
