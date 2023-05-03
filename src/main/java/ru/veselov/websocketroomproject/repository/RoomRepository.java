package ru.veselov.websocketroomproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.entity.RoomEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {
    @Query("SELECT r FROM RoomEntity r where r.name= ?1")
    Optional<RoomEntity> findByName(String name);
    @Query("SELECT r FROM RoomEntity r where r.isPrivate=false")
    List<RoomEntity> findAllPublicRooms();
}
