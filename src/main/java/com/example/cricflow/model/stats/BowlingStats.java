package com.example.cricflow.model.stats;

import com.example.cricflow.model.Statistics;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@PrimaryKeyJoinColumn(name = "bowling_stat_id")
public class BowlingStats extends Statistics {

    @Column
    private int ballsBowled;

    @Column
    private int wicketsTaken;

    @Column
    private int runsConceded;

}
