package com.example.cricflow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "STAT_SEQUENCE",
            sequenceName = "STAT_SEQ",
            allocationSize = 1
    )
    private Long statId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inning_id")
    @JsonIgnore
    Inning inning;
}
