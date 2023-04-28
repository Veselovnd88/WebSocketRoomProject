package ru.veselov.websocketroomproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Room {
    private UUID uuid;
    private String name;
    @JsonProperty("isPrivate")
    private boolean isPrivate;
    private String sourceUrl;
    private String roomToken;
    private String ownerName;

}
