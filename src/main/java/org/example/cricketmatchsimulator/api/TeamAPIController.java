package org.example.cricketmatchsimulator.api;

import org.example.cricketmatchsimulator.entities.Player;
import org.example.cricketmatchsimulator.entities.Team;
import org.example.cricketmatchsimulator.services.TeamService;
import org.springframework.web.bind.annotation.*;

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
}
