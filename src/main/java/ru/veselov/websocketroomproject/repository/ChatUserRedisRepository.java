package ru.veselov.websocketroomproject.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatUserRedisRepository extends CrudRepository<ChatUserEntity, String> {

    Set<ChatUserEntity> findAllByRoomId(String roomId);

    Optional<ChatUserEntity> findById(String sessionId);

}