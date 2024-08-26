package com.example.cricflow.model;

import com.example.cricflow.model.literal.ExcludedFromToString;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class Ground {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "GROUND_SEQUENCE",
            sequenceName = "GROUND_SEQ",
            allocationSize = 1
    )
    private Long groundId;

    @Column(length = 30)
    @NotNull(message = "Ground Name can not be null")
    @Size(min = 5, max = 30, message = "ground name must be 5-30 characters long")
    private String groundName;

    @Override
    public String toString() {
        return generateObjectString(this);
    }


    public boolean groundEquals(Ground other) {
        if (this == other) return true;
        if (other == null) return false;

        System.out.println(this);

        return groundName.equals(other.groundName);
    }


}
