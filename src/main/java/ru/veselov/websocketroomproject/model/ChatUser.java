package ru.veselov.websocketroomproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatUser {
    private String username;
    private String roomId;
    private String session;
}
