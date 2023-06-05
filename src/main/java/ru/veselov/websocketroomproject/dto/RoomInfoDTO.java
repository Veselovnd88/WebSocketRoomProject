package ru.veselov.websocketroomproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoDTO {

    private UUID uuid;
    private String name;
    private boolean isPrivate;
    private String activeUrl;
    private String roomToken;
    private String ownerName;

}
