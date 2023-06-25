package ru.veselov.websocketroomproject.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.NotCorrectOwnerException;
import ru.veselov.websocketroomproject.exception.InvalidRoomTokenException;
import ru.veselov.websocketroomproject.exception.RoomAlreadyExistsException;
import ru.veselov.websocketroomproject.repository.RoomRepository;

import java.security.Principal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RoomValidatorImplTest {
    @Mock
    RoomRepository roomRepository;

    @InjectMocks
    RoomValidatorImpl roomValidator;

    Principal principal = Mockito.mock(Principal.class);

    @Test
    void shouldValidateOwner() {
        Mockito.when(principal.getName()).thenReturn(TestConstants.TEST_USERNAME);
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setOwnerName(TestConstants.TEST_USERNAME);

        Assertions.assertThatNoException().isThrownBy(
                () -> roomValidator.validateOwner(principal, roomEntity)
        );
    }

    @Test
    void shouldThrowNotCorrectOwnerException() {
        Mockito.when(principal.getName()).thenReturn(TestConstants.TEST_USERNAME);
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setOwnerName("Not" + TestConstants.TEST_USERNAME);

        Assertions.assertThatThrownBy(
                () -> roomValidator.validateOwner(principal, roomEntity)
        ).isInstanceOf(NotCorrectOwnerException.class);
    }

    @Test
    void shouldValidateToken() {
        String token = "token";
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setRoomToken("token");

        Assertions.assertThatNoException().isThrownBy(
                () -> roomValidator.validateToken(roomEntity, token)
        );
    }

    @Test
    void shouldThrowNotCorrectTokenException() {
        String token = "token";
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setRoomToken("AnotherToken");

        Assertions.assertThatThrownBy(
                () -> roomValidator.validateToken(roomEntity, token)
        ).isInstanceOf(InvalidRoomTokenException.class);
    }

    @Test
    void shouldThrowRoomAlreadyExistsException() {
        String roomName = "roomName";
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(roomName);
        Mockito.when(roomRepository.findByName(roomName)).thenReturn(Optional.of(roomEntity));

        Assertions.assertThatThrownBy(
                () -> roomValidator.validateRoomName(roomName)
        ).isInstanceOf(RoomAlreadyExistsException.class);
    }

    @Test
    void shouldValidateRoom() {
        String roomName = "roomName";
        Mockito.when(roomRepository.findByName(roomName)).thenReturn(Optional.empty());

        Assertions.assertThatNoException().isThrownBy(
                () -> roomValidator.validateRoomName(roomName)
        );
    }

}