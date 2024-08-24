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
public class Over {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "OVER_SEQUENCE",
            sequenceName = "OVER_SEQ",
            allocationSize = 1
    )
    private Long overId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "over_balls_relation",
            joinColumns = @JoinColumn(referencedColumnName = "overId", name = "over_id"),
            inverseJoinColumns = @JoinColumn(referencedColumnName = "ballId", name = "ball_id")
    )
    List<Ball> balls;

    @Override
    public String toString() {
        return generateObjectString(this);
    }


    public boolean overEquals(Over other) {
        if (this == other) return true;
        if (other == null) return false;

        if (balls.size() != other.balls.size()) return false;

        for (int i = 0; i < balls.size(); i++){
            if (!balls.get(i).ballEquals(other.balls.get(i)))
                return false;
        }

        System.out.println(this);

        return true;
    }
}
