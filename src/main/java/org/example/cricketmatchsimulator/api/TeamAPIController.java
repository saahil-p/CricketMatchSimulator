package org.example.cricketmatchsimulator.api;

import org.example.cricketmatchsimulator.entities.Player;
import org.example.cricketmatchsimulator.entities.Team;
import org.example.cricketmatchsimulator.services.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamAPIController {

    private final TeamService teamService;

    public TeamAPIController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("")
    public Team createTeam(@RequestParam String name, @RequestBody Player[] players){
        return teamService.createTeam(name, players);
    }

    @GetMapping("/search/searchByName")
    public Team getTeamByName(@RequestParam String name){
        return teamService.findByName(name);
    }

    @GetMapping("/search/searchById")
    public Team getTeamById(@RequestParam String teamId){
        return teamService.findById(teamId);
    }

    @GetMapping("/search/all")
    public List<Team> getAllTeams(){
        return teamService.findAll();
    }

}
