package com.example.cricflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "TEAM_SEQUENCE",
            sequenceName = "TEAM_SEQ",
            allocationSize = 1
    )
    private Long teamId;

    @Column
    private String teamName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "team_player_relation",
            joinColumns = @JoinColumn(referencedColumnName = "teamId", name = "team_id"),
            inverseJoinColumns = @JoinColumn(referencedColumnName = "playerId", name = "player_id")
    )
    List<Player> players = new ArrayList<>();

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean teamEquals(Team other) {
        if (this == other) return true;
        if (other == null) return false;


        if (players == null && other.players == null);
        else if ((players == null && other.players != null) || (players != null && other.players == null) || (players.size() != other.players.size()))
            return false;
        else{
            for (int i = 0; i < players.size(); i++)
                if (!players.get(i).playerEquals(other.players.get(i)))
                    return false;
        }
        System.out.println(this);

        return teamName.equals(other.teamName);
    }


}
