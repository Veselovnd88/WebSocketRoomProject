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
        log.info("ChatUser with [session: {}] saved to repository ", chatUser.getSession());
        ChatUserEntity entity = chatUserEntityMapper.toChatUserEntity(chatUser);
        repository.save(entity);
    }

    @Override
    public ChatUser findChatUserBySessionId(String sessionId) {
        log.info("ChatUser with [session: {}] retrieved from repository", sessionId);
        Optional<ChatUserEntity> chatUserEntityOptional = repository.findById(sessionId);
        ChatUserEntity userEntity = chatUserEntityOptional.
                orElseThrow(() -> new ChatUserNotFoundException("No such ChatUser in repository"));
        return chatUserEntityMapper.toChatUser(userEntity);
    }

    @Override
    public Set<ChatUser> findChatUsersByRoomId(String roomId) {
        log.info("ChatUser of [room: {}] retrieved from repository", roomId);
        Set<ChatUserEntity> roomUsers = repository.findAllByRoomId(roomId);
        return chatUserEntityMapper.toChatUsersSet(roomUsers);
    }

    @Override
    public ChatUser removeChatUser(String sessionId) {
        log.info("ChatUser with [session: {}] deleted from repository", sessionId);
        Optional<ChatUserEntity> foundById = repository.findById(sessionId);
        if (foundById.isPresent()) {
            repository.delete(foundById.get());
            return chatUserEntityMapper.toChatUser(foundById.get());
        } else {
            throw new ChatUserNotFoundException("No such ChatUser in repository");
        }
    }

}