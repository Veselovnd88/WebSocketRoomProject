package ru.veselov.websocketroomproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ChatMessage {
    private String sentFrom;
    private String content;
    private ZonedDateTime sent;

}