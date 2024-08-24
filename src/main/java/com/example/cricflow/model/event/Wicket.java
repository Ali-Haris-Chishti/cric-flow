package com.example.cricflow.model.event;

import com.example.cricflow.model.BallEvent;
import com.example.cricflow.model.Player;
import com.example.cricflow.model.literal.WicketType;
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
@PrimaryKeyJoinColumn(name = "wicket_id")
public class Wicket extends BallEvent {

    @ManyToOne
    @JoinColumn(name = "fielder_id")
    Player fielder;

    @Column
    @Enumerated(EnumType.STRING)
    WicketType wicketType;

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    @Override
    public boolean eventEquals(BallEvent other){
        if(!super.eventEquals(other))
            return false;
        boolean wicketEq = wicketType == ((Wicket) other).wicketType;
        if (wicketEq){
            if (fielder == null) {
                if (((Wicket) other).fielder == null)
                    return true;
                else
                    return false;
            }
            else
                return fielder.equals(((Wicket) other).fielder);

        }
        return false;
    }

}
