package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.mapper.ChatUserEntityMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.repository.ChatUserRedisRepository;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.List;
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
    }

    @Override
    public ChatUser findChatUserBySessionId(String sessionId) {
        Optional<ChatUserEntity> chatUserEntityOptional = repository.findById(sessionId);
        ChatUserEntity userEntity = chatUserEntityOptional.orElseThrow(RuntimeException::new);
        return chatUserEntityMapper.toChatUser(userEntity);
    }

    @Override
    public Set<ChatUser> findChatUsersByRoomId(String roomId) {
        List<ChatUserEntity> roomUsers = repository.findAllByRoomId(roomId);
        return chatUserEntityMapper.toChatUser(roomUsers);
    }

    @Override
    public ChatUser removeChatUser(String sessionId) {
        repository.deleteById(sessionId);
        return null;
    }
}
