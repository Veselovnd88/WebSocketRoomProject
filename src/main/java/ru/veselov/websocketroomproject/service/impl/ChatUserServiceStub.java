package ru.veselov.websocketroomproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "local", name = "stub", havingValue = "enabled")
public class ChatUserServiceStub implements ChatUserService {

    private final Faker faker = new Faker();

    public void saveChatUser(ChatUser chatUser) {
        log.info("ChatUser {} saved to db", chatUser.getUsername());
    }

    public ChatUser findChatUserBySessionId(String sessionId) {
        log.info("Retrieving ChatUser with sessionId: {}", sessionId);
        return new ChatUser(
                faker.name().username(),
                "5",
                sessionId
        );
    }

    @Override
    public Set<ChatUser> findChatUsersByRoomId(String roomId) {
        return new HashSet<>(
                faker.collection(
                        () -> new ChatUser(faker.name().username(), "5", faker.expression("#{letterify '???????'}"))
                ).maxLen(4).generate());
    }

    @Override
    public ChatUser removeChatUser(String sessionId) {
        return null;
    }

}