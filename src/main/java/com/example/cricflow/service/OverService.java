package com.example.cricflow.service;

import com.example.cricflow.exception.common.EntityDoesNotExistsException;
import com.example.cricflow.model.Inning;
import com.example.cricflow.model.Match;
import com.example.cricflow.model.Over;
import com.example.cricflow.repository.InningRepo;
import com.example.cricflow.repository.MatchRepo;
import com.example.cricflow.repository.OverRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.*;

import java.util.List;
import java.util.Optional;

@Service
public class OverService {

    private final OverRepo overRepo;
    private final InningRepo inningRepo;
    private final MatchRepo matchRepo;

    public final static String referencedName = "OVER";

    @Autowired
    public OverService(OverRepo overRepo, InningRepo inningRepo, MatchRepo matchRepo) {
        this.overRepo = overRepo;
        this.inningRepo = inningRepo;
        this.matchRepo = matchRepo;
    }


    public ResponseEntity<Over> readOver(long overId) throws EntityDoesNotExistsException {
        Over over = checkIfOverExists(overId);
        return new ResponseEntity<>(over, OK);
    }

    public ResponseEntity<List<Over>> readAllOversOfInnings(long inningId) throws EntityDoesNotExistsException {
        Inning inning = checkIfInningExists(inningId);
        return new ResponseEntity<>(inning.getOvers(), OK);
    }

    public ResponseEntity<List<Over>[]> readAllOversOfMatch(long matchId) throws EntityDoesNotExistsException {
        Match match = checkIfMatchExists(matchId);
        List<Over>[] overLists = new List[2];
        overLists[0] = match.getFirstInnings().getOvers();
        overLists[1] = match.getSecondInnings().getOvers();
        return new ResponseEntity<>(overLists, OK);
    }


    private Over checkIfOverExists(long overId) throws EntityDoesNotExistsException {
        Optional<Over> optionalOver = overRepo.findById(overId);
        if(optionalOver.isEmpty())
            throw new EntityDoesNotExistsException(referencedName, overId);
        return optionalOver.get();
    }

    private Inning checkIfInningExists(long inningId) throws EntityDoesNotExistsException {
        Optional<Inning> optionalInning = inningRepo.findById(inningId);
        if (optionalInning.isEmpty())
            throw new EntityDoesNotExistsException(referencedName, inningId);
        return optionalInning.get();
    }

    private Match checkIfMatchExists(long matchId) throws EntityDoesNotExistsException {
        Optional<Match> optionalMatch = matchRepo.findById(matchId);
        if (optionalMatch.isEmpty())
            throw new EntityDoesNotExistsException(referencedName, matchId);
        return optionalMatch.get();
    }
}
