package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.ChatUser;

import java.util.Optional;
import java.util.Set;

/**
 * Service for managing chat user's sessions connected via WebSocket to the chat
 */
public interface ChatUserService {

    void saveChatUser(ChatUser chatUser);

    ChatUser findChatUserBySessionId(String sessionId);

    Set<ChatUser> findChatUsersByRoomId(String roomId);

    Optional<ChatUser> removeChatUser(String sessionId);
}
