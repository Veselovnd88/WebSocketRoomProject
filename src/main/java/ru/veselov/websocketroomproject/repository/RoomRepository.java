package ru.veselov.websocketroomproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.entity.RoomEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {

    @Query("SELECT r FROM RoomEntity r left join fetch r.tags where r.name= ?1")
    Optional<RoomEntity> findByName(String name);

    @Query("SELECT r FROM RoomEntity r where r.isPrivate=false")
    List<RoomEntity> findAllPublicRooms();

    @Query("SELECT r FROM RoomEntity r where r.isPrivate=false ")
    @NonNull
    Page<RoomEntity> findAll(@NonNull Pageable pageable);

    @Query("select r FROM RoomEntity r left join fetch  r.tags t where  t.name= ?1 order by r.createdAt DESC")
    Set<RoomEntity> getAllByTag(String name);//Pageable

}
