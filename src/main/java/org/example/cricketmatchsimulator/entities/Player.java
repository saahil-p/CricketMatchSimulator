package org.example.cricketmatchsimulator.entities;

import lombok.Data;
import lombok.NonNull;
import org.example.cricketmatchsimulator.enums.PlayerRole;

import java.util.UUID;

@Data
public class Player {
    private final String playerId = UUID.randomUUID().toString();

    @NonNull
    private String name;
    @NonNull
    private PlayerRole role;
}
