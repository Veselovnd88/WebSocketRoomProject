package ru.veselov.websocketroomproject.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatUserDTO {
    @JsonProperty("username")
    private String username;

    private String session;

}