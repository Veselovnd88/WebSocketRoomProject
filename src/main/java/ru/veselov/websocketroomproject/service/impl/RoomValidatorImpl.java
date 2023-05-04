package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.NotCorrectOwnerException;
import ru.veselov.websocketroomproject.exception.NotCorrectTokenException;
import ru.veselov.websocketroomproject.exception.RoomAlreadyExistsException;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomValidator;

import java.security.Principal;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomValidatorImpl implements RoomValidator {

    private final RoomRepository roomRepository;

    public void validateOwner(Principal principal, RoomEntity roomEntity) {
        String username = principal.getName();
        if (!StringUtils.equals(username, roomEntity.getOwnerName())) {
            log.error("Only owner can assign new settings or set Url, [{}] is not owner", username);
            throw new NotCorrectOwnerException(
                    String.format("Only owner can assign new settings or set Url, [%s] is not owner", username));
        }
        log.info("Owner for [room {}] validated", roomEntity.getId());
    }

    public void validateToken(RoomEntity roomEntity, String token) {
        if (!StringUtils.equals(roomEntity.getRoomToken(), token)) {
            log.error("Not correct [token: {}]", token);
            throw new NotCorrectTokenException(String.format("Not correct token %s for access to private room", token));
        }
        log.info("Token for private [room {}] validated", roomEntity.getId());
    }

    public void validateRoomName(String name) {
        Optional<RoomEntity> byName = roomRepository.findByName(name);
        if (byName.isPresent()) {
            log.error("Room with [name {}] already exists", name);
            throw new RoomAlreadyExistsException(String.format("Room with such name [%s] already exists", name));
        }
        log.info("Room with [name {}] can be created/renamed", name);
    }

}