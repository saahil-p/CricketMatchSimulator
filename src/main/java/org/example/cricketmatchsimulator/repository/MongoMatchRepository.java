package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Match;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MongoMatchRepository extends MongoRepository<Match, String>, MatchRepositoryInterface {

    @Query("{ $and: [ { $or: [ { 'team1.name': ?0, 'team2.name': ?1 }, { 'team1.name': ?1, 'team2.name': ?0 } ] }, { 'matchDate': ?2 } ] }")
    Optional<List<Match>> findByTeam1AndTeam2(String team1, String team2, LocalDate date);

    @Query("{ $or: [ { 'team1.name': ?0 }, { 'team2.name': ?0 } ] }")
    Optional<List<Match>> findAllMatchesByTeam(String team);
}
