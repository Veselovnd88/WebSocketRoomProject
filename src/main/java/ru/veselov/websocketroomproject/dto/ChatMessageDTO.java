package ru.veselov.websocketroomproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    @JsonProperty("message")
    private String message;
    @JsonProperty("from")
    private String from;

    @JsonProperty("type")
    private MessageType type;

    @JsonProperty("sentTime")
    private LocalDateTime sentTime;
}
