package ru.veselov.websocketroomproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.veselov.websocketroomproject.model.ChatUser;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatUserDTO {
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("isOwner")
    private Boolean isOwner;


    public static ChatUserDTO convertToChatUserDTO(ChatUser chatUser){
        return new ChatUserDTO(chatUser.getUserId(), chatUser.getUserName(),
                chatUser.getIsOwner());
    }
}
