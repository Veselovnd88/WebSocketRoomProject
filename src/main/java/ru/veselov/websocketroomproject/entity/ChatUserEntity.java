package ru.veselov.websocketroomproject.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("ChatUser")
public class ChatUserEntity {
    @Id
    private String session;

    private String username;

    @Indexed
    private String roomId;


}
