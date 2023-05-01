package ru.veselov.websocketroomproject.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.entity.RoomEntity;

import java.util.UUID;

@Repository
@Transactional
public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {
}
