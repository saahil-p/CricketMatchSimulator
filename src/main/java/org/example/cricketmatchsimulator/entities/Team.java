package org.example.cricketmatchsimulator.entities;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "teams")
@Data
public class Team {
    @Id
    private String teamId = UUID.randomUUID().toString();
    @NonNull
    private String name;
    @NonNull
    private List<Player> players;
}
