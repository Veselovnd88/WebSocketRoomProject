package ru.veselov.websocketroomproject.dto.request;

import lombok.Data;

@Data
public class RoomCreationDTO {

    private String roomName;

    private Boolean isPrivate;
}
