package com.example.cricflow;

import com.example.cricflow.repository.*;
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
public class CricFlowApplication{

	public static void main(String[] args) {
		SpringApplication.run(CricFlowApplication.class, args);
	}

	@Autowired
	PlayerRepo playerRepo;

	@Autowired TeamRepo teamRepo;

	@Autowired TossRepo tossRepo;

	@Autowired MatchRepo matchRepo;

	@Autowired InningRepo inningsRepo;

	@Autowired OverRepo overRepo;

	@Autowired BallRepo ballRepo;

	@Autowired EventRepo eventRepo;



	@Bean
	@Transactional
	CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
//
//			player1 = playerRepo.save(player1);
//			player2 = playerRepo.save(player2);
//			player3 = playerRepo.save(player3);
//
//			ball1 = ballRepo.save(ball1);
//			ball2 = ballRepo.save(ball2);
//			ball3 = ballRepo.save(ball3);
//			ball4 = ballRepo.save(ball4);
//
//			event1.setBall(ball1);
//			event2.setBall(ball2);
//			event3.setBall(ball3);
//			event4.setBall(ball4);
//
//			eventRepo.saveAll(new ArrayList<>(Arrays.asList(event1, event2, event3, event4)));
//
//			ball1.setBallEvent(event1);
//			ball1.setBallEvent(event2);
//			ball1.setBallEvent(event3);
//			ball1.setBallEvent(event4);
//			ballRepo.saveAll(new ArrayList<>(Arrays.asList(ball1, ball2, ball3, ball4)));
//
//
//			System.out.println(ball1);
//			System.out.println(ball2);
//			System.out.println(ball3);
//			System.out.println(ball4);

		};
	}

}
