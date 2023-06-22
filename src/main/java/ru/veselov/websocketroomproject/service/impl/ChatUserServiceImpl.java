package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.exception.ChatUserNotFoundException;
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
    public ChatUser findChatUserBySessionId(String sessionId) {
        ChatUserEntity userEntity = findBySessionId(sessionId);
        log.info("ChatUser with [session: {}] retrieved from repository", sessionId);
        return chatUserEntityMapper.toChatUser(userEntity);
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
        if (chatUserEntityOptional.isPresent()) {
            ChatUserEntity chatUserEntity = chatUserEntityOptional.get();
            repository.delete(chatUserEntity);
            log.info("ChatUser with [session: {}] deleted from repository", sessionId);
            return Optional.of(chatUserEntityMapper.toChatUser(chatUserEntity));
        }
        return Optional.empty();
    }

    private ChatUserEntity findBySessionId(String sessionId) {
        Optional<ChatUserEntity> chatUserEntityOptional = repository.findById(sessionId);
        return chatUserEntityOptional.
                orElseThrow(() -> {
                    log.error("ChatUser with [session: {}] not found", sessionId);
                    throw new ChatUserNotFoundException(
                            String.format("No such ChatUser in repository for sessionId %s", sessionId));
                });
    }

}