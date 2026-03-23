package org.example.cricketmatchsimulator.entities;

import lombok.Data;
import lombok.NonNull;
import org.example.cricketmatchsimulator.enums.PlayerRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "players")
@Data
public class Player {
    @Id
    private String playerId = UUID.randomUUID().toString();

    @NonNull
    private String name;
    @NonNull
    private PlayerRole role;
}
