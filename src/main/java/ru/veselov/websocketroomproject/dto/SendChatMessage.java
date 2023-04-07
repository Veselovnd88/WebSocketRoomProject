package ru.veselov.websocketroomproject.dto;

import lombok.Data;

@Data
public class SendChatMessage {
    private String sentFrom;
    private String content;
    private String sentTime;

}