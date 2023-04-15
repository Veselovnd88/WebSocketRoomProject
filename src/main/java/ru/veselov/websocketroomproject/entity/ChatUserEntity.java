package ru.veselov.websocketroomproject.entity;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("ChatUser")
public class ChatUserEntity {

    private String username;
    private String roomId;
    private String session;

}
