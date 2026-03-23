package org.example.cricketmatchsimulator.api;

import org.example.cricketmatchsimulator.dto.ScoreCardResponse;
import org.example.cricketmatchsimulator.entities.Match;
import org.example.cricketmatchsimulator.enums.MatchType;
import org.example.cricketmatchsimulator.services.MatchService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchApiController {

    private final MatchService matchService;

    public MatchApiController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("")
    public List<Match> getMatchByTeam1andTeam2(
            @RequestParam String team1,
            @RequestParam String team2,
            @RequestParam LocalDate matchDate
    ) {
        return matchService.getMatchByTeam1AndTeam2(team1, team2, matchDate);
    }

    @GetMapping("/by-team")
    public List<Match> getMatchByTeam(@RequestParam String team) {
        return matchService.getAllMatchesByTeam(team);
    }

    @PostMapping("")
    public Match createMatch(@RequestParam String team1, @RequestParam String team2, @RequestParam MatchType matchType, @RequestParam LocalDate matchDate) {
        return matchService.createMatch(team1, team2, matchType, matchDate);
    }

    @GetMapping("/simulate")
    public Match simulateMatch(@RequestParam String team1, @RequestParam String team2, @RequestParam MatchType matchType, @RequestParam LocalDate matchDate) {
        return matchService.simulateMatch(team1, team2, matchType, matchDate);
    }

    @GetMapping("/scorecard")
    public ScoreCardResponse getScoreCard(@RequestParam String team1Name, @RequestParam String team2Name, @RequestParam LocalDate matchDate){
        Match currentMatch = matchService.getMatchByTeam1AndTeam2(team1Name, team2Name, matchDate).stream().findFirst().orElseThrow(() -> new RuntimeException("Match not found"));
     return matchService.generateMatchScoreCard(currentMatch);
    }
}