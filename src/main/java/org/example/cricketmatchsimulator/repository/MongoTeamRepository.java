package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoTeamRepository extends MongoRepository<Team, String>, TeamRepositoryInterface {

    @Query("{ 'name': ?0 }")
    Optional<Team> findByName(String teamName);
}
