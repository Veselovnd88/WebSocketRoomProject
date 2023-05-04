package ru.veselov.websocketroomproject.dto;

import lombok.Data;

@Data
public class RoomSettingsDTO {

    private String id;

    private String roomName;

    private String ownerName;

    private Boolean isPrivate;

    private String playerType;

    private Boolean changeToken;

}
