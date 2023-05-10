package ru.veselov.websocketroomproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedChatMessage {

    private String sentFrom;
    private String content;
    private String sendTo;

}
