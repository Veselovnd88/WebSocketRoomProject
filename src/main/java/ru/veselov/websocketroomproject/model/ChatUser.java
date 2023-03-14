package ru.veselov.websocketroomproject.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {
    private Integer userId;
    private Integer roomId;
    private String session;
    private String userName;
    private String destination;
    private Boolean isOwner;
}
