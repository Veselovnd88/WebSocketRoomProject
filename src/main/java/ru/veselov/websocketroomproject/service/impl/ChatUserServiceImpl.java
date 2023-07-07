package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.mapper.ChatUserEntityMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.repository.ChatUserRedisRepository;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatUserServiceImpl implements ChatUserService {

    private final ChatUserRedisRepository repository;

    private final ChatUserEntityMapper chatUserEntityMapper;

    @Override
    public void saveChatUser(ChatUser chatUser) {
        ChatUserEntity entity = chatUserEntityMapper.toChatUserEntity(chatUser);
        repository.save(entity);
        log.info("ChatUser with [session: {}] saved to repository ", chatUser.getSession());
    }

    @Override
    public Set<ChatUser> findChatUsersByRoomId(String roomId) {
        Set<ChatUserEntity> roomUsers = repository.findAllByRoomId(roomId);
        log.info("ChatUser of [room: {}] retrieved from repository", roomId);
        return chatUserEntityMapper.toChatUsersSet(roomUsers);
    }

    @Override
    public Optional<ChatUser> removeChatUser(String sessionId) {
        Optional<ChatUserEntity> chatUserEntityOptional = repository.findById(sessionId);
        Optional<ChatUserEntity> chatUserEntity = chatUserEntityOptional.map(chatUser -> {
            repository.delete(chatUser);
            log.info("ChatUser with [session: {}] deleted from repository", sessionId);
            return chatUser;
        });
        return Optional.ofNullable(chatUserEntityMapper.toChatUser(chatUserEntity.orElse(null)));
    }

}