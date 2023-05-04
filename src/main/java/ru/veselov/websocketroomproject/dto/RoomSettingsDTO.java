package ru.veselov.websocketroomproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RoomSettingsDTO {

    private String id;

    private String roomName;

    @JsonProperty("ownername")
    private String ownerName;

    private Boolean isPrivate;

    private String playerType;

    private Boolean changeToken;



}
