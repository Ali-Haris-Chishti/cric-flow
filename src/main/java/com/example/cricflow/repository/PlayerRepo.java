package com.example.cricflow.repository;

import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepo extends JpaRepository<Player, Long> {
    List<Player> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    List<Player> findAllByTeamIsNull();
    List<Player> findAllByTeamIsNotNull();

    List<Player> findAllByPlayerType(Player.PlayerType playerType);
    List<Player> findAllByBattingStyle(Player.BattingStyle battingStyle);
    List<Player> findAllByBowlingStyle(Player.BowlingStyle bowlingStyle);
    List<Player> findAllByPlayerTypeAndBattingStyle(Player.PlayerType playerType, Player.BattingStyle battingStyle);
    List<Player> findAllByPlayerTypeAndBowlingStyle(Player.PlayerType playerType, Player.BowlingStyle bowlingStyle);
    List<Player> findAllByBattingStyleAndBowlingStyle(Player.BattingStyle battingStyle, Player.BowlingStyle bowlingStyle);
    List<Player> findAllByPlayerTypeAndBattingStyleAndBowlingStyle(Player.PlayerType playerType, Player.BattingStyle battingStyle, Player.BowlingStyle bowlingStyle);

    @Query("SELECT p FROM Player p WHERE LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :nameSequence, '%'))")
    List<Player> findAllByFullNameContaining(@Param("nameSequence") String nameSequence);


}
