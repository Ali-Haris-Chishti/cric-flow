package com.example.cricflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TeamPlayerRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "TPR_SEQUENCE",
            sequenceName = "TPR_SEQ",
            allocationSize = 1
    )
    private Long relationId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private LocalDate startDate;
    private LocalDate endDate;

    // Constructors, getters, setters, etc.

//    @Embeddable
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class TeamPlayerId implements java.io.Serializable {
//        private Long teamId;
//        private Long playerId;
//    }

}

