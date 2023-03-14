package ru.veselov.websocketroomproject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessage {
    private String content;
    private String from;
    private LocalDateTime time;
}
