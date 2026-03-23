package org.example.cricketmatchsimulator.dto;

import lombok.Data;

@Data
public class PlayerStats {
    private String playerName;
    private int runsScored;
    private int ballsPlayed;
    private Integer ballNumberWhenOut; // null if not out
    private int fours;
    private int sixes;
    private boolean isOut;
    
    public PlayerStats(String playerName) {
        this.playerName = playerName;
        this.runsScored = 0;
        this.ballsPlayed = 0;
        this.ballNumberWhenOut = null;
        this.fours = 0;
        this.sixes = 0;
        this.isOut = false;
    }
    
    public void addRuns(int runs) {
        this.runsScored += runs;
        this.ballsPlayed++;
        
        if (runs == 4) {
            this.fours++;
        } else if (runs == 6) {
            this.sixes++;
        }
    }
    
    public void setOut(int ballNumber) {
        this.isOut = true;
        this.ballNumberWhenOut = ballNumber;
    }
    
    public double getStrikeRate() {
        if (ballsPlayed == 0) {
            return 0.0;
        }
        return (runsScored * 100.0) / ballsPlayed;
    }
}

