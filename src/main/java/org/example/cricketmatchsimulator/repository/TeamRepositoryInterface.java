package org.example.cricketmatchsimulator.repository;

import org.example.cricketmatchsimulator.entities.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepositoryInterface {

    Team save(Team team);

    Optional<Team> findById(String teamId);

    Optional<Team> findByName(String teamName);

    List<Team> findAll();

}