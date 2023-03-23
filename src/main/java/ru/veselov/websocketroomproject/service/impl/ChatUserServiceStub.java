package ru.veselov.websocketroomproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "local", name = "stub", havingValue = "enabled")
public class ChatUserServiceStub implements ChatUserService {

    public void saveChatUser(ChatUser chatUser) {
        log.info("ChatUser {} saved to db", chatUser.getUsername());
    }

    public ChatUser getChatUserBySessionId(String sessionId) {
        log.info("Retrieving ChatUser with sessionId: {}", sessionId);
        return new ChatUser(
                "test",
                "5",
                sessionId
        );
    }

    @Override
    public ChatUser removeChatUser(String sessionId) {
        log.info("User with Session {} removed",sessionId);
        return new ChatUser(
                "test",
                "5",
                sessionId
        );
    }

}
