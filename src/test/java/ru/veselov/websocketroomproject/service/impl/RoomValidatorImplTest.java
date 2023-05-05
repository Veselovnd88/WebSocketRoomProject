package ru.veselov.websocketroomproject.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.NotCorrectOwnerException;
import ru.veselov.websocketroomproject.exception.NotCorrectTokenException;
import ru.veselov.websocketroomproject.exception.RoomAlreadyExistsException;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomValidator;

import java.security.Principal;
import java.util.Optional;

@SpringBootTest
class RoomValidatorImplTest {
    @MockBean
    RoomRepository roomRepository;

    @Autowired
    RoomValidator roomValidator;

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
        ).isInstanceOf(NotCorrectTokenException.class);
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