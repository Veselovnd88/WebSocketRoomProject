package ru.veselov.websocketroomproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.entity.TagEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    @Query("SELECT t FROM TagEntity t WHERE t.tagId=?1")
    Optional<TagEntity> findByTagId(Long id);

    @Query("SELECT t FROM TagEntity t join fetch t.rooms WHERE t.name=?1")
    Optional<TagEntity> findByName(String name);

    @Query("SELECT t FROM TagEntity t")
    List<TagEntity> findAll();

}
