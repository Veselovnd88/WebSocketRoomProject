package ru.veselov.websocketroomproject.dto;

import lombok.Data;

@Data
public class RoomInfoDTO {

    private String owner;
    private String roomId;

    private String username;
}
