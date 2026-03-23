package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepositoryInterface {
    Player save(Player player);

    Optional<Player> findById(String playerId);

    Optional<Player> findByName(String playerName);

    List<Player> findAll();
}
