package ru.veselov.websocketroomproject.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.model.ChatUser;

import java.util.Set;

public interface ChatUserRedisRepository {
    void saveChatUserToRoom(String roomId, ChatUserEntity chatUserEntity);

    ChatUserEntity findChatUser(String sessionId);

    void removeChatUserFromRoom(ChatUserEntity chatUserEntity);

    Set<ChatUserEntity> getChatUsersFromRoom(String roomId);


}
