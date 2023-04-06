package ru.veselov.websocketroomproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceivedChatMessage {

    private String sentFrom;
    private String content;

}
