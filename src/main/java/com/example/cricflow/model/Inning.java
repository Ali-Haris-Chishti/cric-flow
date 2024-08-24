package com.example.cricflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Inning {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "INNINGS_SEQUENCE",
            sequenceName = "INNINGS_SEQ",
            allocationSize = 1
    )
    private Long inningId;

    @Column
    int numberOfOvers;

    @ManyToOne
    @JoinColumn(name = "batting_team_id")
    Team battingSide;

    @ManyToOne
    @JoinColumn(name = "bowling_team_id")
    Team bowlingSide;

    @OneToMany(
            targetEntity = Over.class,
            fetch = FetchType.EAGER
    )
    List<Over> overs;


    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean inningEquals(Inning other) {
        if (this == other) return true;
        if (other == null) return false;

        if (overs.size() != other.overs.size()) return false;
        for (int i = 0; i < overs.size(); i++) {
            if (!overs.get(i).overEquals(other.overs.get(i)))
                return false;
        }

        System.out.println(this);

        return numberOfOvers == other.numberOfOvers
                && battingSide.teamEquals(other.battingSide)
                && bowlingSide.teamEquals(other.bowlingSide)
                ;
    }

}
