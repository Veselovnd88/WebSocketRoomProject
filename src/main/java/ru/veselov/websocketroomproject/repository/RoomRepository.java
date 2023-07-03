package ru.veselov.websocketroomproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.entity.RoomEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {

    @Query("SELECT r FROM RoomEntity r left join fetch r.tags where r.name= :name")
    Optional<RoomEntity> findByName(@Param("name") String name);

    @Query(value = "SELECT r FROM RoomEntity r left join fetch  r.tags where r.isPrivate=false",
    countQuery ="SELECT COUNT(r) FROM RoomEntity r left join r.tags where r.isPrivate=false")
    @NonNull
    Page<RoomEntity> findAllPublicRooms(@NonNull Pageable pageable);

    @Query(value = "SELECT r FROM RoomEntity r left join fetch r.tags t where t.name=:tag and r.isPrivate=false",
            countQuery = "SELECT COUNT(r) FROM RoomEntity r left join r.tags t where t.name=:tag and r.isPrivate=false")
    Page<RoomEntity> findAllByTag(@Param("tag") String tag, @NonNull Pageable pageable);

}
