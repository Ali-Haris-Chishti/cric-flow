package com.example.cricflow;

import com.example.cricflow.model.Team;
import com.example.cricflow.repository.*;
import com.example.cricflow.service.PlayerService;
import com.example.cricflow.service.TeamService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class CricFlowApplication extends BaseData{

	public static void main(String[] args) {
		SpringApplication.run(CricFlowApplication.class, args);
	}

//	@Autowired
//	PlayerRepo playerRepo;
//
//	@Autowired TeamRepo teamRepo;
//
//	@Autowired TossRepo tossRepo;
//
//	@Autowired MatchRepo matchRepo;
//
//	@Autowired InningRepo inningsRepo;
//
//	@Autowired OverRepo overRepo;
//
//	@Autowired BallRepo ballRepo;
//
//	@Autowired EventRepo eventRepo;



//	@Bean
//	@Transactional
//	CommandLineRunner commandLineRunner(ApplicationContext ctx, TeamService teamService, PlayerService playerService) {
//		return args -> {
//			player1 = playerRepo.save(player1);
//			player2 = playerRepo.save(player2);
//			player3 = playerRepo.save(player3);
//			player4 = playerRepo.save(player4);
//		};
//	}
}
