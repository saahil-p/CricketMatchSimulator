package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//@Repository  // Disabled to use MongoDB repository instead
public class InMemoryPlayerRepositoryImpl implements PlayerRepositoryInterface{

    private final Map<String, Player> playerStorage = new HashMap<>();

    @Override
    public Player save(Player player){
        playerStorage.putIfAbsent(player.getPlayerId(),player);
        return player;
    }

    @Override
    public Optional<Player> findById(String playerId){
        return Optional.of(playerStorage.get(playerId));
    }
    @Override
    public Optional<Player> findByName(String playerName){
        return playerStorage.values()
                .stream()
                .filter(player -> player.getName().equals(playerName))
                .findAny();
    }
    @Override
    public List<Player> findAll(){
        return playerStorage.values().stream().toList();
    }
}
