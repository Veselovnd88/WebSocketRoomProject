package ru.veselov.websocketroomproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoDTO {

    private String owner;

    private String roomId;

    private String username;

    private String roomToken;

    private String roomName;
}
