package com.example.cricflow.repository;

import com.example.cricflow.model.Inning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InningRepo extends JpaRepository<Inning, Long> {
    List<Inning> findAllByInningIdAndInningStatus(long inningId, Inning.InningStatus inningStatus);
}
