package ru.veselov.websocketroomproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsDTO {

    private String id;

    private String roomName;

    private String ownerName;

    private Boolean isPrivate;

    private String playerType;

    private Boolean changeToken;

}
