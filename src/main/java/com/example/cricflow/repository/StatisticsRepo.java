package com.example.cricflow.repository;

import com.example.cricflow.model.Inning;
import com.example.cricflow.model.Player;
import com.example.cricflow.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepo extends JpaRepository<Statistics, Long> {
    Statistics findByInningAndPlayer(Inning inning, Player player);
    List<Statistics> findAllByInning(Inning inning);
}
