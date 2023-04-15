package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.repository.ChatUserRedisRepository;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "local", name = "stub", havingValue = "enabled")
@RequiredArgsConstructor
public class ChatUserServiceStub implements ChatUserService {

    private final ChatUserRedisRepository chatUserRedisRepository;

    private static final String ROOM_ID = "5";

    private final Faker faker = new Faker();

    private final Map<String, ChatUser> stubRepository = new HashMap<>();

    public void saveChatUser(ChatUser chatUser) {
        stubRepository.put(chatUser.getSession(), chatUser);
        ChatUserEntity chatUserEntity = new ChatUserEntity();
        chatUserEntity.setUsername(chatUser.getUsername());
        chatUserEntity.setSession(chatUser.getSession());
        chatUserEntity.setRoomId(chatUser.getRoomId());
        chatUserRedisRepository.saveChatUserToRoom(chatUser.getRoomId(), chatUserEntity);
        log.info("ChatUser {} saved to db", chatUser.getUsername());
    }

    public ChatUser findChatUserBySessionId(String sessionId) {
        ChatUserEntity chatUser = chatUserRedisRepository.findChatUser(sessionId);
        log.warn("found {}", chatUser);
        log.info("Retrieving ChatUser with sessionId: {}", sessionId);
        return stubRepository.get(sessionId);
    }

    @Override
    public Set<ChatUser> findChatUsersByRoomId(String roomId) {
        Set<ChatUserEntity> chatUsersFromRoom = chatUserRedisRepository.getChatUsersFromRoom(roomId);
        log.warn("---{}", chatUsersFromRoom);
        log.info("Retrieve all users of room #{}", roomId);
        return new HashSet<>(
                faker.collection(this::generateUser).maxLen(4).generate());
    }

    @Override
    public ChatUser removeChatUser(String sessionId) {
        ChatUserEntity chatUserEntity = new ChatUserEntity();
        ;
        chatUserEntity.setSession(sessionId);
        chatUserRedisRepository.removeChatUserFromRoom(chatUserEntity);
        log.info("Removing ChatUser with sessionId: {}", sessionId);
        return stubRepository.remove(sessionId);
    }

    private ChatUser generateUser() {
        return new ChatUser(faker.name().username(), ROOM_ID, faker.expression("#{letterify '???????'}"));
    }

}