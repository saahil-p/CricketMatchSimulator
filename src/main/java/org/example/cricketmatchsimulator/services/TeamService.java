package org.example.cricketmatchsimulator.services;

import org.example.cricketmatchsimulator.entities.Player;
import org.example.cricketmatchsimulator.entities.Team;
import org.example.cricketmatchsimulator.repository.TeamRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepositoryInterface teamRepository;

    public TeamService(TeamRepositoryInterface teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team createTeam(String name, Player[] players){
        Team team = new Team(name, List.of(players));
        return teamRepository.save(team);
    }

    public Team findById(String teamId){
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
    }

    public Team findByName(String teamName){
        return teamRepository.findByName(teamName)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamName));
    }

    public List<Team> findAll(){
        return teamRepository.findAll();
    }
}
