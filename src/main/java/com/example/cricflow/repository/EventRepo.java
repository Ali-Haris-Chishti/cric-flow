package com.example.cricflow.repository;

import com.example.cricflow.model.BallEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRepo extends JpaRepository<BallEvent, Long> {
    @Query("SELECT COUNT(be) FROM BallEvent be WHERE (TYPE(be) = Wicket OR TYPE(be) = Score) AND TYPE(be) <> Extra")
    long countWicketOrScoreEventsButNotExtra();
}
