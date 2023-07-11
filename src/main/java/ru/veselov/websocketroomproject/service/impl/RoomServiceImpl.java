package ru.veselov.websocketroomproject.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.event.handler.RoomDeleteEventHandler;
import ru.veselov.websocketroomproject.exception.PageExceedsMaximumValueException;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.validation.RoomValidator;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    @Value("${server.zoneId}")
    private String zoneId;

    @Value("${room.rooms-per-page}")
    private int roomsPerPage;

    private ZoneId zone;

    private final RoomMapper roomMapper;

    private final RoomRepository roomRepository;

    private final TagRepository tagRepository;

    private final RoomValidator roomValidator;

    private final RoomDeleteEventHandler roomDeleteEventHandler;

    @PostConstruct
    public void init() {
        zone = ZoneId.of(zoneId);
    }

    @Override
    @Transactional
    public Room createRoom(Room room, Principal principal) {
        String ownerName = principal.getName();
        roomValidator.validateRoomName(room.getName());//checks is it possible to save
        RoomEntity roomEntity = roomMapper.toEntity(room);
        roomEntity.setCreatedAt(ZonedDateTime.now(zone));
        roomEntity.setOwnerName(ownerName);
        setRoomTokenIfPrivate(roomEntity);
        setRoomTags(room, roomEntity);
        RoomEntity saved = roomRepository.save(roomEntity);
        log.info("[Saved room {}]", saved);
        return roomMapper.entityToRoom(saved);
    }

    @Override
    public Room getRoomById(String id, String token) {
        RoomEntity roomEntity = findRoomById(id);
        if (Boolean.TRUE.equals(roomEntity.getIsPrivate())) {
            roomValidator.validateToken(roomEntity, token);
        }
        log.info("Retrieving [room {}] from repo", id);
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    public Room getRoomByName(String name) {
        Optional<RoomEntity> foundRoom = roomRepository.findByName(name);
        RoomEntity roomEntity = foundRoom.orElseThrow(
                () -> {
                    log.error("No room found with [name={}]", name);
                    throw new RoomNotFoundException(String.format("No room found with name [%s]", name));
                }
        );
        log.info("Retrieving [room {}] from repo", name);
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    public List<Room> findAll(int page, String sort, String order) {
        long roomCount = roomRepository.countAllPublicRooms();
        validatePageNumber(page, roomCount);
        Pageable pageable = createPageable(page, sort, order);
        Page<RoomEntity> found = roomRepository.findAllPublicRooms(pageable);
        log.info("Found [{} rooms] on {} page and {} sort", found.getContent().size(), page, sort);
        return roomMapper.entitiesToRooms(found.getContent());
    }

    @Override
    public List<Room> findAllByTag(String tag, int page, String sort, String order) {
        Pageable pageable = createPageable(page, sort, order);
        long roomCount = roomRepository.countAllPublicRoomsByTag(tag);
        validatePageNumber(page, roomCount);
        Page<RoomEntity> found = roomRepository.findAllByTag(tag, pageable);
        log.info("Found [{} rooms] on {} page and {} sorting and tag {}", found.getContent().size(), page, sort, tag);
        return roomMapper.entitiesToRooms(found.getContent());
    }

    @Override
    public void deleteRoom(String roomId) {
        findRoomById(roomId);//checks if room exists
        roomDeleteEventHandler.handleRoomDeleteEvent(roomId);
    }

    private RoomEntity findRoomById(String id) {
        UUID uuid = UUID.fromString(id);
        Optional<RoomEntity> foundRoom = roomRepository.findById(uuid);
        return foundRoom.orElseThrow(
                () -> {
                    log.error("No room found with [id={}]", id);
                    throw new RoomNotFoundException(String.format("No room found with id [%s]", id));
                }
        );
    }

    private void setRoomTokenIfPrivate(RoomEntity roomEntity) {
        if (Boolean.TRUE.equals(roomEntity.getIsPrivate())) {
            roomEntity.setRoomToken(RandomStringUtils.randomAlphanumeric(10));
        }
    }

    private void setRoomTags(Room room, RoomEntity roomEntity) {
        for (Tag t : room.getTags()) {
            Optional<TagEntity> tagOptional = tagRepository.findByName(t.getName());
            tagOptional.ifPresent(roomEntity::addTag);
        }
    }

    private Pageable createPageable(int page, String sort, String order) {
        Sort sortOrder;
        if (StringUtils.equals(order, "asc")) {
            sortOrder = Sort.by(sort).ascending();
        } else {
            sortOrder = Sort.by(sort).descending();
        }
        return PageRequest.of(page, roomsPerPage).withSort(sortOrder);
    }

    private void validatePageNumber(int page, long roomCount) {
        long totalPages = roomCount / roomsPerPage;
        if (page > totalPages) {
            log.error("Page number exceeds maximum value [max: {}, was: {}}]", totalPages, page);
            throw new PageExceedsMaximumValueException("Page number exceeds maximum value [max: %s, was: %s]"
                    .formatted(totalPages, page), page);
        }
    }

}
