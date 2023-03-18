package ru.veselov.websocketroomproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatUser {
    private Integer userId;
    private Integer roomId;
    private String session;
    private String username;
    private String destination;
    private Boolean isOwner;
}
