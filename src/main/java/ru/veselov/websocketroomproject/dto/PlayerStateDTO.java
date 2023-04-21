package ru.veselov.websocketroomproject.dto;

import lombok.Data;

@Data
public class PlayerStateDTO {
    private int playerState;
    private String currentTime;

    private String playbackQuality;

    private String playbackRate;

}
