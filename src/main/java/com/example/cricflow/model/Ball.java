package com.example.cricflow.model;

import com.example.cricflow.model.literal.ExcludedFromToString;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Ball {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "BALL_SEQUENCE",
            sequenceName = "BALL_SEQ",
            allocationSize = 1
    )
    private Long ballId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private BallEvent ballEvent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "striker_id")
    Player striker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "non_striker_id")
    Player nonStriker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bowler_id")
    Player bowler;

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean ballEquals(Ball other) {
        if (this == other) return true;
        if (other == null) return false;

        System.out.println(this);

        return
                striker.playerEquals(other.striker)
                        && nonStriker.playerEquals(other.nonStriker)
                        && bowler.playerEquals(other.bowler)
                        && ballEvent.eventEquals(other.ballEvent);
    }
}
