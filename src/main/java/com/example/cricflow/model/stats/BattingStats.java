package com.example.cricflow.model.stats;

import com.example.cricflow.model.Statistics;
import jakarta.persistence.*;
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
@PrimaryKeyJoinColumn(name = "batting_stat_id")
public class BattingStats extends Statistics {

    @Column
    private int runsScored;

    @Column
    private int ballsFaced;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    public static enum Status {
        OUT,
        NOT_OUT
    }
}
