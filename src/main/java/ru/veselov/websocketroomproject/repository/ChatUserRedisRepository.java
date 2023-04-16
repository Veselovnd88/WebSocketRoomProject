package ru.veselov.websocketroomproject.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatUserRedisRepository extends CrudRepository<ChatUserEntity, String> {

    List<ChatUserEntity> findAllByRoomId(String roomId);

    Optional<ChatUserEntity> findById(String sessionId);

}