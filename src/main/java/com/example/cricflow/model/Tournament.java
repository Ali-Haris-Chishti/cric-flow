package com.example.cricflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "TOSS_SEQUENCE",
            sequenceName = "TOSS_SEQ",
            allocationSize = 1
    )
    private Long tournamentId;

    @Column
    private String tournamentName;

    @OneToMany(
            targetEntity = Match.class,
            cascade = CascadeType.REMOVE
    )
    @JoinTable(
            name = "tournament_matches",
            joinColumns = @JoinColumn(referencedColumnName = "tournamentId", name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(referencedColumnName = "matchId", name = "match_id")
    )
    Set<Match> matches;

    @OneToOne
    Team winner;

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean tournamentEquals(Tournament other) {
        if (this == other) return true;
        if (other == null) return false;

        System.out.println(this);

        return tournamentName.equals(other.tournamentName);
    }

}
