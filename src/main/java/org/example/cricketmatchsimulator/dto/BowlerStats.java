package org.example.cricketmatchsimulator.dto;

import lombok.Data;

@Data
public class BowlerStats {
    private String bowlerName;
    private int oversBowled;
    private int ballsBowled;
    private int runsConceded;
    private int wicketsTaken;
    private int maidenOvers;
    
    public BowlerStats(String bowlerName) {
        this.bowlerName = bowlerName;
        this.oversBowled = 0;
        this.ballsBowled = 0;
        this.runsConceded = 0;
        this.wicketsTaken = 0;
        this.maidenOvers = 0;
    }
    
    public void addBall(int runs, boolean isWicket) {
        this.ballsBowled++;
        this.runsConceded += runs;
        
        if (isWicket) {
            this.wicketsTaken++;
        }
    }
    
    public void completeOver(boolean isMaiden) {
        this.oversBowled++;
        if (isMaiden) {
            this.maidenOvers++;
        }
    }
    
    public double getEconomy() {
        if (oversBowled == 0 && ballsBowled == 0) {
            return 0.0;
        }
        double totalOvers = oversBowled + (ballsBowled / 6.0);
        return runsConceded / totalOvers;
    }
    
    public String getOversDisplay() {
        return oversBowled + "." + ballsBowled;
    }
}

