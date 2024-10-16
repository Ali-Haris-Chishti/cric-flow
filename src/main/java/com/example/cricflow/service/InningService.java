package com.example.cricflow.service;

import com.example.cricflow.controller.InningController;
import com.example.cricflow.exception.common.EntityDoesNotExistsException;
import com.example.cricflow.exception.inning.*;
import com.example.cricflow.exception.team.PlayerNotInTeamException;
import com.example.cricflow.model.*;
import com.example.cricflow.model.event.Extra;
import com.example.cricflow.model.event.Score;
import com.example.cricflow.model.event.Wicket;
import com.example.cricflow.model.literal.WicketType;
import com.example.cricflow.model.stats.BattingStats;
import com.example.cricflow.model.stats.BowlingStats;
import com.example.cricflow.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.http.HttpStatus.OK;

@Service
public class InningService {

    private final InningRepo inningRepo;
    private final MatchRepo matchRepo;
    private final PlayerRepo playerRepo;
    private final StatisticsRepo statisticsRepo;
    private final OverRepo overRepo;
    private final BallRepo ballRepo;

    public static String referencedName = "INNING";

    private boolean overFinishedFlag = false;
    private boolean strikeRotationFlag = false;

    @Autowired
    public InningService(InningRepo inningRepo, MatchRepo matchRepo, PlayerRepo playerRepo, StatisticsRepo statisticsRepo, OverRepo overRepo, BallRepo ballRepo) {
        this.inningRepo = inningRepo;
        this.matchRepo = matchRepo;
        this.playerRepo = playerRepo;
        this.statisticsRepo = statisticsRepo;
        this.overRepo = overRepo;
        this.ballRepo = ballRepo;
    }

    public ResponseEntity<Inning> readInning(long inningId) throws EntityDoesNotExistsException {
        Inning inning = checkIfInningExists(inningId);
        return new ResponseEntity<>(inning, OK);
    }

    public ResponseEntity<Inning []> getInningsOfAMatch(long matchId) throws EntityDoesNotExistsException {
        Match match = checkIfMatchExists(matchId);
        Inning [] innings = new Inning[2];
        innings[0] = match.getFirstInnings();
        innings[1] = match.getSecondInnings();
        return new ResponseEntity<>(innings, OK);
    }

    public ResponseEntity<List<Inning>> readAllInnings() {
        List<Inning> innings = inningRepo.findAll();
        return new ResponseEntity<>(innings, OK);
    }

    public ResponseEntity<List<Statistics>> getInningsStats(long inningId) {
        Inning inning = checkIfInningExists(inningId);
        List<Statistics> statistics = statisticsRepo.findAllByInning(inning);
        return new ResponseEntity<>(statistics, OK);
    }

    public ResponseEntity<Inning> initializeInning(long matchId, InningController.BatsmanBowlerInitializr initializr)
            throws EntityDoesNotExistsException, BatsmanBowlerInitializationException, PlayerNotInTeamException, InningInitializationException {
        Match match = checkIfMatchExists(matchId);
        Player striker = checkIfPlayerExists(initializr.strikerId());
        Player nonStriker = checkIfPlayerExists(initializr.nonStrikerId());
        Player bowler = checkIfPlayerExists(initializr.bowlerId());
        Inning currentInning = getCurrentInningAndValidate(match.getFirstInnings(), match.getSecondInnings());
        currentInning.setInningStatus(Inning.InningStatus.STARTED);
        validateBatsmanAndBowlerConstraints(striker, nonStriker, bowler, currentInning.getBattingSide(), currentInning.getBowlingSide());
        Over over = Over.builder()
                .bowler(bowler)
                .balls(List.of(Ball.builder()
                                .striker(striker)
                                .nonStriker(nonStriker)
                        .build()))
                .build();
        List<Over> overs = new ArrayList<>();
        overs.add(over);
        currentInning.setOvers(overs);
        currentInning = inningRepo.save(currentInning);

        BattingStats battingStats1 = BattingStats.builder()
                .player(striker)
                .status(BattingStats.Status.NOT_OUT)
                .inning(currentInning)
                .build();
        BattingStats battingStats2 = BattingStats.builder()
                .player(nonStriker)
                .status(BattingStats.Status.NOT_OUT)
                .inning(currentInning)
                .build();
        BowlingStats bowlingStats = BowlingStats.builder()
                .player(bowler)
                .inning(currentInning)
                .build();
        statisticsRepo.saveAll(Arrays.asList(battingStats1, battingStats2, bowlingStats));

        return new ResponseEntity<>(currentInning, OK);
    }

    @Transactional
    public ResponseEntity<Ball> addBall(long matchId, BallEvent event) {
        if (overFinishedFlag)
            throw new OverFinishedException();
        Match match = checkIfMatchExists(matchId);
        Inning currentInning = getCurrentInning(match.getFirstInnings(), match.getSecondInnings());
        Over over = overRepo.findTopByOrderByOverIdDesc();
        over.getBalls().getLast().setBallEvent(event);
        over = overRepo.save(over);

        currentInning = inningRepo.save(currentInning);
        Ball ball = ballRepo.findTopByOrderByBallIdDesc();
        initializeNextBall(currentInning, event, ball.getStriker(), ball.getNonStriker(), over.getBowler());

        return new ResponseEntity<>(ball, OK);
    }

    @Transactional
    public ResponseEntity<BallEvent[]> addMultipleBalls(long matchId, BallEvent[] events) {
        for (BallEvent event: events)
            addBall(matchId, event);
        return new ResponseEntity<>(events, OK);
    }

    @Transactional
    public ResponseEntity<Over> addBowlerForOver(long matchId, long playerId){
        Match match = checkIfMatchExists(matchId);
        Inning currentInning = getCurrentInning(match.getFirstInnings(), match.getSecondInnings());
        Player bowler = checkIfPlayerExists(playerId);

        if (!bowler.getTeam().equals(currentInning.getBowlingSide()))
            throw new PlayerNotInTeamException(playerId, "Bowler", currentInning.getBattingSide().getTeamName());

        Over lastOver = overRepo.findTopByOrderByOverIdDesc();
        if (lastOver.getBalls() == null || lastOver.getBalls().size() != 1 || lastOver.getBowler() != null)
            throw new AddingBowlerBeforeNewOverException(bowler.getPlayerId());

        lastOver.setBowler(bowler);
        if (statisticsRepo.findByInningAndPlayer(currentInning, bowler) == null) {
            BowlingStats bowlingStats = BowlingStats.builder()
                    .player(bowler)
                    .inning(currentInning)
                    .build();
            statisticsRepo.save(bowlingStats);
        }
        overFinishedFlag = false;

        return new ResponseEntity<>(lastOver, OK);
    }

    private Inning checkIfInningExists(long inningId) throws EntityDoesNotExistsException{
        Optional<Inning> inning = inningRepo.findById(inningId);
        if (inning.isEmpty())
            throw new EntityDoesNotExistsException(referencedName, inningId);
        return inning.get();
    }

    private Match checkIfMatchExists(long matchId) throws EntityDoesNotExistsException {
        Optional<Match> match = matchRepo.findById(matchId);
        if (match.isEmpty())
            throw new EntityDoesNotExistsException(referencedName, matchId);
        return match.get();
    }

    private Player checkIfPlayerExists(long playerId) throws EntityDoesNotExistsException {
        Optional<Player> optionalPlayer = playerRepo.findById(playerId);
        if (optionalPlayer.isEmpty())
            throw new EntityDoesNotExistsException(PlayerService.referencedClass, playerId);
        return optionalPlayer.get();
    }

    private void validateBatsmanAndBowlerConstraints(Player striker, Player nonStriker, Player bowler, Team teamA, Team teamB) throws BatsmanBowlerInitializationException, PlayerNotInTeamException{
        if (Objects.equals(striker.getPlayerId(), nonStriker.getPlayerId()))
            throw new BatsmanBowlerInitializationException(striker.getPlayerId(), "STRIKER", "NON-STRIKER");
        if (Objects.equals(striker.getPlayerId(), bowler.getPlayerId()))
            throw new BatsmanBowlerInitializationException(striker.getPlayerId(), "STRIKER", "BOWLER");
        if (Objects.equals(nonStriker.getPlayerId(), bowler.getPlayerId()))
            throw new BatsmanBowlerInitializationException(nonStriker.getPlayerId(), "NON_STRIKER", "BOWLER");

        if (!teamA.equals(striker.getTeam()))
            throw new PlayerNotInTeamException(striker.getPlayerId(), "STRIKER", "Batting");
        if (!teamA.equals(nonStriker.getTeam()))
            throw new PlayerNotInTeamException(striker.getPlayerId(), "NON-STRIKER", "Batting");
        if (!teamB.equals(bowler.getTeam()))
            throw new PlayerNotInTeamException(striker.getPlayerId(), "BOWLER", "Bowling");
    }

    private Inning getCurrentInningAndValidate(Inning firstInning, Inning secondInning
    )  throws BatsmanBowlerInitializationException, PlayerNotInTeamException, InningInitializationException {
        Inning.InningStatus firstInningStatus = firstInning.getInningStatus();
        Inning.InningStatus secondInningStatus = secondInning.getInningStatus();
        if (firstInningStatus == Inning.InningStatus.YET_TO_START) {
            return firstInning;
        }
        if (firstInningStatus == Inning.InningStatus.STARTED)
            throw new InningInitializationException(firstInning.getInningId(), "STARTED");
        if (secondInningStatus == Inning.InningStatus.STARTED)
            throw new InningInitializationException(secondInning.getInningId(), "STARTED");
        if (secondInningStatus == Inning.InningStatus.FINISHED)
            throw new InningInitializationException(secondInning.getInningId(), "FINISHED");
        return secondInning;
    }

    private Inning getCurrentInning(Inning firstInning, Inning secondInning){
        Inning.InningStatus firstInningStatus = firstInning.getInningStatus();
        Inning.InningStatus secondInningStatus = secondInning.getInningStatus();
        if (firstInningStatus == Inning.InningStatus.STARTED)
            return firstInning;
        if (secondInningStatus == Inning.InningStatus.STARTED)
            return secondInning;
        if (firstInningStatus == Inning.InningStatus.YET_TO_START)
            throw new InningInitializationException("Could not add ball because first inning is yet to start, initialize it first");
        if (firstInningStatus == Inning.InningStatus.FINISHED && secondInningStatus == Inning.InningStatus.FINISHED)
            throw new InningInitializationException("Could not add ball because match has already been finished");
        if (firstInningStatus == Inning.InningStatus.FINISHED && secondInningStatus == Inning.InningStatus.YET_TO_START)
            throw new InningInitializationException("Could not add ball because second inning is yet to start, initialize it first");
        return secondInning;
    }

    private void initializeNextBall(Inning currentInning, BallEvent event, Player striker, Player nonStriker, Player bowler){
        if (event.getClass().equals(Score.class))
            handleScoreEvent((Score) event, currentInning, striker, nonStriker, bowler);
        else if (event.getClass().equals(Wicket.class))
            handleWicketEvent((Wicket) event, currentInning, striker, nonStriker, bowler);
        else if (event.getClass().equals(Extra.class))
            handleExtraEvent((Extra) event, currentInning, striker, nonStriker, bowler);
        if (strikeRotationFlag){
            Player temp = striker;
            striker = nonStriker;
            nonStriker = temp;
            strikeRotationFlag = false;
        }
        try {
            if (checkOverCompletion(currentInning)){
                Player temp = striker;
                striker = nonStriker;
                nonStriker = temp;
                overFinishedFlag = true;
            }
        } catch (InningCompletionException e) {
            currentInning.setInningStatus(Inning.InningStatus.FINISHED);
            return;
        }
        updateBallWithEvent(currentInning, striker, nonStriker);
    }

    private void handleScoreEvent(Score score, Inning currentInning, Player striker, Player nonStriker, Player bowler){
        BattingStats battingStats = (BattingStats) statisticsRepo.findByInningAndPlayer(currentInning, striker);
        battingStats.setBallsFaced(battingStats.getBallsFaced() + 1);
        battingStats.setRunsScored(battingStats.getRunsScored() + score.getScoreType().getScore());
        BowlingStats bowlingStats = (BowlingStats) statisticsRepo.findByInningAndPlayer(currentInning, bowler);
        bowlingStats.setBallsBowled(bowlingStats.getBallsBowled() + 1);
        bowlingStats.setRunsConceded(bowlingStats.getRunsConceded() + score.getScoreType().getScore());
        statisticsRepo.saveAll(Arrays.asList(battingStats, bowlingStats));
        if (score.getScoreType().getScore() % 2 == 1)
            strikeRotationFlag = true;
    }

    private void handleWicketEvent(Wicket wicket, Inning currentInning, Player striker, Player nonStriker, Player bowler){
        BattingStats battingStats = (BattingStats) statisticsRepo.findByInningAndPlayer(currentInning, striker);
        battingStats.setStatus(BattingStats.Status.OUT);
        battingStats.setBallsFaced(battingStats.getBallsFaced() + 1);
        BowlingStats bowlingStats = (BowlingStats) statisticsRepo.findByInningAndPlayer(currentInning, bowler);
        bowlingStats.setBallsBowled(bowlingStats.getBallsBowled() + 1);
        if (wicket.getWicketType() == WicketType.BOWLED || wicket.getWicketType() == WicketType.CAUGHT)
            bowlingStats.setWicketsTaken(bowlingStats.getWicketsTaken() + 1);
        statisticsRepo.saveAll(Arrays.asList(battingStats, bowlingStats));
        striker = null;
    }

    private void handleExtraEvent(Extra extra, Inning currentInning, Player striker, Player nonStriker, Player bowler){
        BattingStats battingStats = (BattingStats) statisticsRepo.findByInningAndPlayer(currentInning, striker);
        battingStats.setBallsFaced(battingStats.getBallsFaced() + 1);
        battingStats.setRunsScored(battingStats.getRunsScored() + extra.getScoreType().getScore());
        BowlingStats bowlingStats = (BowlingStats) statisticsRepo.findByInningAndPlayer(currentInning, bowler);
        bowlingStats.setRunsConceded(bowlingStats.getRunsConceded() + extra.getScoreType().getScore());
        statisticsRepo.saveAll(Arrays.asList(battingStats, bowlingStats));
        if (extra.getScoreType().getScore() % 2 == 1)
            strikeRotationFlag = true;
    }

    private void handleOverCompletion(){

    }

    private void updateBallWithEvent(Inning currentInning, Player striker, Player nonStriker){
        Ball ball = new Ball(null, null, striker, nonStriker);
        List<Ball> balls = new ArrayList<>();
        Over over = overRepo.findTopByOrderByOverIdDesc();
        if (over.getBalls() != null)
            balls.addAll(over.getBalls());
        balls.add(ball);
        over.setBalls(balls);
        inningRepo.save(currentInning);
    }

    private boolean checkOverCompletion(Inning currentInning) throws InningCompletionException {
        long noOfBalls = overRepo.countValidDeliveriesOfLastOver();
        System.out.println("No Of Balls: " + noOfBalls);
        if (noOfBalls == 6){
            if (currentInning.getOvers().size() == currentInning.getNumberOfOvers()) {
                System.out.println("Innings Completed");
                throw new InningCompletionException();
            }
            List<Over> overs = new ArrayList<>();
            overs.addAll(currentInning.getOvers());
            overs.add(new Over());
            currentInning.setOvers(overs);
            return true;
        }
        return false;
    }
}
