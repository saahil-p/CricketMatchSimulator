package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Match;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class InMemoryMatchRepositoryImpl implements MatchRepositoryInterface {
    private final Map<String, Match> matchStorage = new HashMap<>();

    @Override
    public Match save(Match match) {
        matchStorage.put(match.getMatchId(), match);
        return match;
    }

    @Override
    public Optional<Match> findById(String matchId){
        return Optional.of(matchStorage.get(matchId));
    }

    @Override
    public Optional<List<Match>> findByTeam1AndTeam2(String team1, String team2, LocalDate date){
        return Optional.of(matchStorage.values().stream()
                .filter(match -> (match.getTeam1().getName().equals(team1) && match.getTeam2().getName().equals(team2))
                        || (match.getTeam1().getName().equals(team2) && match.getTeam2().getName().equals(team1)))
                .filter(match -> match.getMatchDate().equals(date))
                .toList());
    }

    @Override
    public Optional<List<Match>> findAllMatchesByTeam(String team){
        return Optional.of(matchStorage.values().stream().filter(match-> match.getTeam1().getName().equals(team) || match.getTeam2().getName().equals(team)).toList());
    }
}
