package ru.veselov.websocketroomproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.websocketroomproject.entity.PlayerType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Room implements Serializable {
    private UUID id;
    private String name;
    @JsonProperty("isPrivate")
    private Boolean isPrivate;
    private String sourceUrl;
    private String roomToken;
    private String ownerName;

    private PlayerType playerType;

    private ZonedDateTime createdAt;


    private ZonedDateTime changedAt;

}
