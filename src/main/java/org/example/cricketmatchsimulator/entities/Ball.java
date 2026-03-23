package org.example.cricketmatchsimulator.entities;

import lombok.Data;
import lombok.NonNull;

@Data
public class Ball {
//    @NonNull
//    private Innings innings;
    @NonNull
    private Integer overNumber;
    @NonNull
    private Integer ballNumber;
    @NonNull
    private Player batter;
    @NonNull
    private Player bowler;
    @NonNull
    private Integer runs;
    @NonNull
    private Boolean isWicket;
    @NonNull
    private Boolean isExtra;
}
