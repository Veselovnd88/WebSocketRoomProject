package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.ChatUser;

import java.util.Set;

public interface ChatUserService {
    void saveChatUser(ChatUser chatUser);

    ChatUser findChatUserBySessionId(String sessionId);

    Set<ChatUser> findChatUsersByRoomId(String roomId);

    ChatUser removeChatUser(String sessionId);
}
