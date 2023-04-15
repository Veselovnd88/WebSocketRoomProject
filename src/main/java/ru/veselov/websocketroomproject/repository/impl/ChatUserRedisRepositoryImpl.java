package ru.veselov.websocketroomproject.repository.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.repository.ChatUserRedisRepository;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatUserRedisRepositoryImpl implements ChatUserRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, ChatUserEntity> hashOps;
    private SetOperations<String, Object> setOperations;


    @PostConstruct
    private void init() {
        hashOps = redisTemplate.opsForHash();
        setOperations = redisTemplate.opsForSet();
    }

    @Override
    public void saveChatUserToRoom(String roomId, ChatUserEntity chatUserEntity) {
        hashOps.put(roomId, chatUserEntity.getSession(), chatUserEntity);
        setOperations.add(chatUserEntity.getSession(), chatUserEntity);
    }

    @Override
    public ChatUserEntity findChatUser(String sessionId) {
        ChatUserEntity chatUser = (ChatUserEntity) setOperations.randomMembers(sessionId, 1);
        return chatUser;
    }

    @Override
    public void removeChatUserFromRoom(ChatUserEntity chatUserEntity) {
        ChatUserEntity chatUser = (ChatUserEntity) setOperations.pop(chatUserEntity.getSession());
        hashOps.delete(chatUser.getRoomId(), chatUserEntity.getSession());
    }

    @Override
    public Set<ChatUserEntity> getChatUsersFromRoom(String roomId) {
        return new HashSet<>(hashOps.entries(roomId).values());
    }

}