package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Team;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Repository
public class InMemoryTeamRepositoryImpl implements TeamRepositoryInterface {

    private final Map<String, Team> teamStorage = new HashMap<>();

    @Override
    public Team save(Team team){
        teamStorage.putIfAbsent(team.getTeamId(),team);
        return team;
    }
    @Override
    public Optional<Team> findById(String teamId){
        return Optional.of(teamStorage.get(teamId));
    }
    @Override
    public Optional<Team> findByName(String teamName){
        return teamStorage.values().stream().filter(team -> team.getName().equals(teamName)).findAny();
    }
    @Override
    public List<Team> findAll(){
        return teamStorage.values().stream().toList();
    }
}
