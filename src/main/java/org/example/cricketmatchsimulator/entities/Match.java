package org.example.cricketmatchsimulator.entities;

import lombok.Data;
import lombok.NonNull;
import org.example.cricketmatchsimulator.enums.MatchType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "matches")
@Data
public class Match {
    @Id
    private String matchId = UUID.randomUUID().toString();
    @NonNull
    private Team team1;
    @NonNull
    private Team team2;

    private int team1Score;
    private int team2Score;

    @NonNull
    private MatchType matchType;
    @NonNull
    private LocalDate matchDate = LocalDate.now();

    private List<Innings> innings = new ArrayList<>();

    public int getOvers(){
        return matchType.getOvers();
    }

    private String tossWinner;
    private String matchWinner;


}
