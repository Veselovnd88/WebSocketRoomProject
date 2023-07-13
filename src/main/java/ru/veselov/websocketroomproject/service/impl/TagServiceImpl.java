package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.mapper.TagMapper;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;
import ru.veselov.websocketroomproject.service.TagService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final RoomRepository roomRepository;

    private final TagMapper tagMapper;

    @Override
    public Set<Tag> getTags() {
        log.info("Retrieving tags from DB");
        return tagMapper.toTags(new HashSet<>(tagRepository.findAll()));
    }

    @Transactional
    @Override
    public Set<Tag> deleteTag(String name) {
        Optional<TagEntity> optionalTag = tagRepository.findByNameWithRooms(name);
        optionalTag.ifPresent(
                (tagEntity -> {
                    tagEntity.getRooms().forEach(r -> {
                        Optional<RoomEntity> optionalRoom = roomRepository.findByName(r.getName());
                        optionalRoom.ifPresent(
                                roomEntity -> {
                                    roomEntity.removeTag(tagEntity);
                                    roomRepository.save(roomEntity);
                                });
                    });
                    tagRepository.delete(tagEntity);
                }
                )
        );
        log.info("Deleting tag [{}]", name);
        return tagMapper.toTags(new HashSet<>(tagRepository.findAll()));
    }

    @Transactional
    @Override
    public Set<Tag> addTag(String name) {
        Optional<TagEntity> tagEntityOptional = tagRepository.findByName(name);
        if (tagEntityOptional.isEmpty()) {
            tagRepository.save(new TagEntity(name));
            log.info("New tag [{}] added", name);
        }
        return tagMapper.toTags(new HashSet<>(tagRepository.findAll()));
    }

}
