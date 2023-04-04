package ru.veselov.websocketroomproject.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String sentFrom;
    private String content;


}
