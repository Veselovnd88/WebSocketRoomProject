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
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.UrlEntity;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.RoomSettingsService;
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

    private ZoneId zone;

    private final RoomMapper roomMapper;

    private final RoomRepository roomRepository;

    private final RoomSettingsService roomSettingsService;

    private final RoomValidator roomValidator;

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
        if (roomEntity.getIsPrivate()) {
            roomEntity.setRoomToken(RandomStringUtils.randomAlphanumeric(10));
        }
        RoomEntity saved = roomRepository.save(roomEntity);
        log.info("[Saved room {}]", saved);
        return roomMapper.entityToRoom(saved);
    }

    @Override
    public Room getRoomById(String id, String token) {
        RoomEntity roomEntity = findRoomById(id);
        if (roomEntity.getIsPrivate()) {
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
    @Transactional
    public Room changeSettings(String roomId, RoomSettingsDTO settingsDTO, Principal principal) {
        RoomEntity roomEntity = findRoomById(roomId);
        roomValidator.validateOwner(principal, roomEntity);
        RoomEntity changedRoomEntity = roomSettingsService.applySettings(roomEntity, settingsDTO);
        RoomEntity saved = roomRepository.save(changedRoomEntity);
        log.info("[Room's {}] settings changed", roomId);
        return roomMapper.entityToRoom(saved);
    }

    @Override
    @Transactional
    public void addUrl(String roomId, String url, Principal principal) {
        RoomEntity roomEntity = findRoomById(roomId);
        roomEntity.setActiveUrl(url);
        UrlEntity urlEntity = new UrlEntity(url, ZonedDateTime.now(zone));
        roomEntity.addUrl(urlEntity);
        roomRepository.save(roomEntity);
        log.info("New [url {}] added to [room {}]", url, roomId);
    }

    public List<Room> findAll(int page, String sorting, String order) {
        if (StringUtils.isBlank(sorting)) {
            sorting = "createdAt";
        }
        Sort sortOrder;
        if (StringUtils.isBlank(order) || order.equals("none")) {
            sortOrder = Sort.by(sorting);
        } else if (order.equals("desc")) {
            sortOrder = Sort.by(sorting).descending();
        } else {
            sortOrder = Sort.by(sorting).ascending();
        }
        Pageable pageable = PageRequest.of(page, 6).withSort(sortOrder);
        Page<RoomEntity> found = roomRepository.findAll(pageable);
        log.info("Found [{} rooms] with on {} page and {} sorting", found.getNumber(), page, sorting);
        return roomMapper.entitiesToRooms(found.getContent());
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

}