package org.example.cricketmatchsimulator.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ScoreCardResponse {
    private String team1Name;
    private String team2Name;

    private int team1Score;
    private int team2Score;

    private int team1Wickets;
    private int team2Wickets;

    private String matchWinner;

    private Map<String, PlayerStats> battingStatsInnings1 = new HashMap<>();
    private Map<String, PlayerStats> battingStatsInnings2 = new HashMap<>();
    private Map<String, BowlerStats> bowlingStatsInnings1 = new HashMap<>();
    private Map<String, BowlerStats> bowlingStatsInnings2 = new HashMap<>();
}
