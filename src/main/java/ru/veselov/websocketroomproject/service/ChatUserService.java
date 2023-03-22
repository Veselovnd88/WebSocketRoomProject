package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.ChatUser;

public interface ChatUserService {
    void saveChatUser(ChatUser chatUser);

    ChatUser getChatUserBySessionId(String sessionId);

}
