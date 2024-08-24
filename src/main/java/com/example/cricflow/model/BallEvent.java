package com.example.cricflow.model;

import com.example.cricflow.model.literal.ExcludedFromToString;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class BallEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "EVENT_SEQUENCE",
            sequenceName = "EVENT_SEQ",
            allocationSize = 1
    )
    private long eventId;

    @OneToOne
    @JoinColumn(name = "ball_id")
    @ExcludedFromToString
    private Ball ball;

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean eventEquals(BallEvent other) {
        if (this == other) return true;
        if (other == null) return false;

        System.out.println(this);

        return ball.getBallId() == other.ball.getBallId();
    }
}
