package ru.veselov.websocketroomproject.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ChatMessage {
    private String sentFrom;
    private String content;
    private ZonedDateTime sent;

}
