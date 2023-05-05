package ru.veselov.websocketroomproject.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.RoomValidator;

import java.util.Optional;

@SpringBootTest
class RoomServiceImplTest {

    private final static String ROOM_ID = "ec1edd63-4080-480b-84cc-2faee587999f";

    @MockBean
    RoomRepository roomRepository;

    @MockBean
    RoomValidator roomValidator;

    @Captor
    ArgumentCaptor<RoomEntity> roomCaptor;

    @Autowired
    RoomService roomService;


    @Test
    void shouldCreatePrivateRoom() {
        Room room = getRoom(true);

        roomService.createRoom(room);

        Mockito.verify(roomRepository, Mockito.times(1)).save(roomCaptor.capture());
        Mockito.verify(roomValidator, Mockito.times(1)).validateRoomName(ArgumentMatchers.anyString());
        RoomEntity captured = roomCaptor.getValue();

        Assertions.assertThat(captured.getCreatedAt()).isNotNull();
        Assertions.assertThat(captured.getIsPrivate()).isTrue();
        Assertions.assertThat(captured.getRoomToken()).isNotBlank();
        Assertions.assertThat(captured.getName()).isEqualTo("MyRoom");
        Assertions.assertThat(captured.getOwnerName()).isEqualTo("User1");
    }

    @Test
    void shouldCreatePublicRoom() {
        Room room = getRoom(false);

        roomService.createRoom(room);

        Mockito.verify(roomRepository, Mockito.times(1)).save(roomCaptor.capture());
        Mockito.verify(roomValidator, Mockito.times(1)).validateRoomName(ArgumentMatchers.anyString());
        RoomEntity captured = roomCaptor.getValue();

        Assertions.assertThat(captured.getCreatedAt()).isNotNull();
        Assertions.assertThat(captured.getIsPrivate()).isFalse();
        Assertions.assertThat(captured.getRoomToken()).isNull();
        Assertions.assertThat(captured.getName()).isEqualTo("MyRoom");
        Assertions.assertThat(captured.getOwnerName()).isEqualTo("User1");
    }

    @Test
    void shouldReturnPrivateRoomById() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(true);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));

        Assertions.assertThatNoException().isThrownBy(
                () -> roomService.getRoomById(ROOM_ID, "token")
        );

        Mockito.verify(roomRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
        Mockito.verify(roomValidator, Mockito.times(1)).validateToken(roomEntity, "token");
    }

    @Test
    void shouldReturnPublicRoomById() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(false);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));

        Assertions.assertThatNoException().isThrownBy(
                () -> roomService.getRoomById(ROOM_ID, null)
        );

        Mockito.verify(roomRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
        Mockito.verify(roomValidator, Mockito.never())
                .validateToken(ArgumentMatchers.any(), ArgumentMatchers.anyString());
    }

    @Test
    void shouldThrowRoomNotFound() {
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> roomService.getRoomById(ROOM_ID, null)
        ).isInstanceOf(RoomNotFoundException.class);

        Mockito.verify(roomRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
        Mockito.verify(roomValidator, Mockito.never())
                .validateToken(ArgumentMatchers.any(), ArgumentMatchers.anyString());
    }

    @Test
    void shouldReturnRoomByName() {
        Mockito.when(roomRepository.findByName(ArgumentMatchers.anyString())).thenReturn(Optional.of(new RoomEntity()));

        Assertions.assertThatNoException().isThrownBy(
                () -> roomService.getRoomByName("RoomName")
        );
        Mockito.verify(roomRepository, Mockito.times(1)).findByName("RoomName");
    }

    @Test
    void shouldThrowRoomNotFoundException() {
        Mockito.when(roomRepository.findByName(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                ()->roomService.getRoomByName("RoomName")
        ).isInstanceOf(RoomNotFoundException.class);

        Mockito.verify(roomRepository, Mockito.times(1)).findByName("RoomName");
    }


    private Room getRoom(boolean isPrivate) {
        return Room.builder()
                .name("MyRoom")
                .isPrivate(isPrivate)
                .playerType(PlayerType.YOUTUBE)
                .ownerName("User1").build();
    }

}