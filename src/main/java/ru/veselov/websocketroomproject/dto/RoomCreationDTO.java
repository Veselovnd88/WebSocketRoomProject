package ru.veselov.websocketroomproject.dto;

import lombok.Data;

@Data
public class RoomCreationDTO {

    private String roomName;

    private Boolean isPrivate;
}
