package ru.veselov.websocketroomproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.Set;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "local", name = "stub", havingValue = "enabled")
public class ChatUserServiceStub implements ChatUserService {

    public void saveChatUser(ChatUser chatUser) {
        log.info("ChatUser {} saved to db", chatUser.getUsername());
    }

    public ChatUser findChatUserBySessionId(String sessionId) {
        log.info("Retrieving ChatUser with sessionId: {}", sessionId);
        return new ChatUser(
                "test",
                "5",
                sessionId
        );
    }

    @Override
    public Set<ChatUser> findChatUsersByRoomId(String roomId) {
        /*DataFaker*/
        return Set.of(
                new ChatUser(
                        "vasya",
                        "5",
                        "session1"
                ),
                new ChatUser(
                        "petya",
                        "5",
                        "session2"
                )
        );
    }

}