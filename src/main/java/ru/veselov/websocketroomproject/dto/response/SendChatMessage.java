package ru.veselov.websocketroomproject.dto.response;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class SendChatMessage {
    private String sentFrom;
    private String content;
    private ZonedDateTime sentTime;
    private String sendTo;

}