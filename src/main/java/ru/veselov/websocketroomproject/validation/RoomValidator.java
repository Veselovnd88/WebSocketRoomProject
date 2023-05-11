package ru.veselov.websocketroomproject.validation;

import ru.veselov.websocketroomproject.entity.RoomEntity;

import java.security.Principal;

public interface RoomValidator {

    void validateRoomName(String name);

    void validateOwner(Principal principal, RoomEntity roomEntity);

    void validateToken(RoomEntity roomEntity, String token);

}