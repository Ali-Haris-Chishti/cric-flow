package com.example.cricflow.model;

import com.example.cricflow.model.event.Extra;
import com.example.cricflow.model.event.Score;
import com.example.cricflow.model.event.Wicket;
import com.example.cricflow.model.literal.ExcludedFromToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.example.cricflow.model.literal.StringGenerator.generateObjectString;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Extra.class, name = "extra"),
        @JsonSubTypes.Type(value = Score.class, name = "score"),
        @JsonSubTypes.Type(value = Wicket.class, name = "wicket")
})
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

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean eventEquals(BallEvent other) {
        if (this == other) return true;
        if (other == null) return false;

        System.out.println(this);
        return true;
    }
}
