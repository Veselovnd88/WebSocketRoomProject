package ru.veselov.websocketroomproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStateDTO {
    private int playerState;

    private String currentTime;

    private String playbackQuality;

    private String playbackRate;

}
