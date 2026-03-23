package org.example.cricketmatchsimulator.entities;

import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
public class Team {
    private final String teamId = UUID.randomUUID().toString();
    @NonNull
    private String name;
    @NonNull
    private List<Player> players;
}
