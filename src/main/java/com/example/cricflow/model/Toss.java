package com.example.cricflow.model;

import com.example.cricflow.model.literal.ExcludedFromToString;
import com.example.cricflow.model.literal.TeamSide;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
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
public class Toss {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "TOSS_SEQUENCE",
            sequenceName = "TOSS_SEQ",
            allocationSize = 1
    )
    private Long tossId;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "match_id")
    @ExcludedFromToString
    Match match;

    @Column
    @Enumerated(EnumType.STRING)
    TeamSide winningSide;

    @Column
    @Enumerated(EnumType.STRING)
    TeamSide battingSide;

    @Column
    @Enumerated(EnumType.STRING)
    TeamSide bowlingSide;

    @PreUpdate
    @Transactional
    void postPersist() {

    }

    @Override
    public String toString() {
        return generateObjectString(this);
    }

    public boolean tossEquals(Toss other){
        if (this == other) return true;
        if (other == null) return false;

        System.out.println(this);

        return winningSide == other.winningSide
                && battingSide == other.battingSide
                && bowlingSide == other.bowlingSide;
    }
}
