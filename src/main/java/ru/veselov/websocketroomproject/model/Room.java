package ru.veselov.websocketroomproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.websocketroomproject.entity.PlayerType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room implements Serializable {
    private UUID id;

    private String name;

    @JsonProperty("isPrivate")
    private Boolean isPrivate;

    private String activeUrl;

    private String roomToken;

    private String ownerName;

    private PlayerType playerType;

    private ZonedDateTime createdAt;

    private ZonedDateTime changedAt;

}
