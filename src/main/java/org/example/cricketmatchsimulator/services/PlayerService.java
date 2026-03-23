package org.example.cricketmatchsimulator.services;

import org.example.cricketmatchsimulator.entities.Player;
import org.example.cricketmatchsimulator.entities.Team;
import org.example.cricketmatchsimulator.enums.PlayerRole;
import org.example.cricketmatchsimulator.repository.PlayerRepositoryInterface;
import org.example.cricketmatchsimulator.repository.TeamRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepositoryInterface playerRepository;
    private final TeamRepositoryInterface teamRepository;

    public PlayerService(PlayerRepositoryInterface playerRepository, TeamRepositoryInterface teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    public Player createPlayer(String name, PlayerRole role){
        Player player = new Player(name, role);
        return playerRepository.save(player);
    }

    public Player findPlayerByName(String playerName){
        return playerRepository.findByName(playerName)
                .orElseThrow(() -> new RuntimeException("Player not found: " + playerName));
    }

    public Player findPlayerById(String playerId){
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found: " + playerId));
    }

    public List<Player> findAllPlayers(){
        return playerRepository.findAll();
    }

}
