package com.example.cricflow.repository;

import com.example.cricflow.model.Over;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OverRepo extends JpaRepository<Over, Long> {

    Over findTopByOrderByOverIdDesc();

    @Query("SELECT COUNT(b) FROM Over o " +
            "JOIN o.balls b " +
            "WHERE o = (SELECT o FROM Over o ORDER BY o.overId DESC LIMIT 1) " +
            "AND (TYPE(b.ballEvent) = Wicket OR TYPE(b.ballEvent) = Score)")
    long countValidDeliveriesOfLastOver();
}
