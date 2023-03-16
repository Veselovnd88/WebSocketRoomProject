package ru.veselov.websocketroomproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatUserDTO {
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("username")
    private String username;
}
