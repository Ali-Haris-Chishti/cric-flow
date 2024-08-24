package com.example.cricflow.model.event;

import com.example.cricflow.model.BallEvent;
import com.example.cricflow.model.literal.ScoreType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@PrimaryKeyJoinColumn(name = "score_id")
public class Score extends BallEvent {

    @Enumerated(EnumType.STRING)
    ScoreType scoreType;

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    @Override
    public boolean eventEquals(BallEvent other){
        if(!super.eventEquals(other))
            return false;
        return scoreType == ((Score) other).scoreType;
    }
}
