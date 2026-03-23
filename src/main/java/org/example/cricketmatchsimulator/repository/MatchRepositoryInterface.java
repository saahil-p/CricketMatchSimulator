package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Match;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchRepositoryInterface {
    Match save(Match match);

    Optional<Match> findById(String matchId);

    Optional<List<Match>> findByTeam1AndTeam2(String team1, String team2, LocalDate date);

    Optional<List<Match>> findAllMatchesByTeam(String team);

}
