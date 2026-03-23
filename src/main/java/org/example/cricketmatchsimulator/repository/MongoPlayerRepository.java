package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoPlayerRepository extends MongoRepository<Player, String>, PlayerRepositoryInterface {

    @Query("{ 'name': ?0 }")
    Optional<Player> findByName(String playerName);
}
