package org.example.cricketmatchsimulator.entities;

import lombok.Data;
import lombok.NonNull;

import java.util.*;

@Data
public class Innings {
    @NonNull
    private Team bowlingTeam;
    private Map<Player, Integer> oversBowled = new HashMap<>();
    @NonNull
    private Team battingTeam;
    @NonNull
    private List<Player> battingOrder;
    private Integer nextBatsmanIndex = 2;
    @NonNull
    private Player striker;
    @NonNull
    private Player nonStriker;
    @NonNull
    private Player bowler;

    private List<Ball> balls = new ArrayList<>();

    private Integer totalRuns = 0;
    private Integer currentOver = 0;
    private Integer currentBallInOver = 0;

    private Boolean isAllOut = false;

    public void addBall(Ball ball){
        balls.add(ball);
    }
}
