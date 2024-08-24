package com.example.cricflow.model.event;

import com.example.cricflow.model.BallEvent;
import com.example.cricflow.model.literal.ExtraType;
import com.example.cricflow.model.literal.ScoreType;
import jakarta.persistence.*;
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
@PrimaryKeyJoinColumn(name = "extra_id")
public class Extra extends BallEvent {

    @Column
    @Enumerated(EnumType.STRING)
    private ExtraType extraType;

    @Column
    @Enumerated(EnumType.STRING)
    private ScoreType scoreType;

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    @Override
    public boolean eventEquals(BallEvent other){
        if(!super.eventEquals(other))
            return false;
        return scoreType == ((Extra) other).scoreType
                && extraType == ((Extra) other).extraType;
    }

}
