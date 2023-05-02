package ru.veselov.websocketroomproject.service.impl;

import jakarta.persistence.TableGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.NotCorrectOwnerException;
import ru.veselov.websocketroomproject.exception.RoomAlreadyExistsException;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final String URL = "/api/room/";

    private final RoomMapper roomMapper;

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public Room createRoom(Room room) {
        String name = room.getName();
        Optional<RoomEntity> byName = roomRepository.findByName(name);
        if (byName.isPresent()) {
            log.warn("Room with [name {}] already exists", name);
            throw new RoomAlreadyExistsException("Room with such name already exists");
        }
        RoomEntity roomEntity = roomMapper.dtoToRoomEntity(room);
        roomEntity.setSourceUrl(URL);
        if (roomEntity.getIsPrivate()) {
            roomEntity.setRoomToken(UUID.randomUUID().toString());
        }
        RoomEntity saved = roomRepository.save(roomEntity);
        log.warn("[Saved room {}]", saved);
        return roomMapper.entityToRoom(saved);
    }

    @Override
    public Room getRoomById(String id) {
        UUID uuid = UUID.fromString(id);
        RoomEntity roomEntity = findRoomById(uuid);
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    public Room getRoomByName(String name) {
        Optional<RoomEntity> foundRoom = roomRepository.findByName(name);
        RoomEntity roomEntity = foundRoom.orElseThrow(
                () -> {
                    log.warn("No room found with [name={}]", name);
                    throw new RoomNotFoundException("No room found with name=" + name);
                }
        );
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    @Transactional
    public Room changeOwner(Room room, Principal principal) {
        RoomEntity roomEntity = findRoomById(room.getId());
        if (roomEntity.getOwnerName().equals(principal.getName())) {
            roomEntity.setOwnerName(room.getOwnerName());
            RoomEntity saved = roomRepository.save(roomEntity);
            return roomMapper.entityToRoom(saved);
        } else {
            throw new NotCorrectOwnerException("Only owner can assign new owner of room");
        }
    }

    @Override
    @Transactional
    public Room changeStatus(Room room, Principal principal) {
        RoomEntity roomEntity = findRoomById(room.getId());
        if (roomEntity.getOwnerName().equals(principal.getName())) {
            roomEntity.setIsPrivate(room.getIsPrivate());
            RoomEntity saved = roomRepository.save(roomEntity);
            return roomMapper.entityToRoom(saved);

        } else {
            throw new NotCorrectOwnerException("Only owner can change status of room");
        }
    }

    @Override
    public List<Room> getAllRooms() {
        return null;
    }

    private RoomEntity findRoomById(UUID id) {
        Optional<RoomEntity> foundRoom = roomRepository.findById(id);
        return foundRoom.orElseThrow(
                () -> {
                    log.warn("No room found with [id={}]", id);
                    throw new RoomNotFoundException("No room found with id=" + id);
                }
        );
    }

}
