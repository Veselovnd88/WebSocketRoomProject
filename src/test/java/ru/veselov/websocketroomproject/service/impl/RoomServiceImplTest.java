package ru.veselov.websocketroomproject.service.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.event.handler.impl.RoomDeleteEventHandlerImpl;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.mapper.RoomMapperImpl;
import ru.veselov.websocketroomproject.mapper.TagMapper;
import ru.veselov.websocketroomproject.mapper.TagMapperImpl;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;
import ru.veselov.websocketroomproject.validation.RoomValidator;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    Faker faker = new Faker();

    @Mock
    RoomRepository roomRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    RoomValidator roomValidator;

    @Mock
    RoomDeleteEventHandlerImpl roomDeleteEventHandler;

    @Mock
    Principal principal;

    @InjectMocks
    RoomServiceImpl roomService;

    @Captor
    ArgumentCaptor<RoomEntity> roomCaptor;

    @BeforeEach
    void init() {
        RoomMapperImpl roomMapper = new RoomMapperImpl();
        ReflectionTestUtils.setField(roomService, "zoneId", "Europe/Moscow", String.class);
        ReflectionTestUtils.setField(roomService, "zone", ZoneId.of("Europe/Moscow"), ZoneId.class);
        ReflectionTestUtils.setField(roomService, "roomMapper", roomMapper, RoomMapper.class);
        ReflectionTestUtils.setField(roomService, "roomsPerPage", 6);
        ReflectionTestUtils.setField(roomMapper, "tagMapper", new TagMapperImpl(), TagMapper.class);
    }

    @Test
    void shouldCreatePrivateRoomWithTags() {
        //given
        Room room = getRoom(true);
        room.setTags(Set.of(
                new Tag("Movie")
        ));
        TagEntity movieTagEntity = new TagEntity("Movie", ZonedDateTime.now());
        String ownerName = faker.elderScrolls().firstName();
        Mockito.when(principal.getName()).thenReturn(ownerName);
        Mockito.when(tagRepository.findByName("Movie"))
                .thenReturn(Optional.of(movieTagEntity));

        //when
        roomService.createRoom(room, principal);

        //then
        Mockito.verify(roomRepository, Mockito.times(1)).save(roomCaptor.capture());
        Mockito.verify(roomValidator, Mockito.times(1)).validateRoomName(ArgumentMatchers.anyString());
        Mockito.verify(tagRepository, Mockito.times(1)).findByName(ArgumentMatchers.anyString());
        RoomEntity captured = roomCaptor.getValue();
        Assertions.assertThat(captured.getCreatedAt()).isNotNull();
        Assertions.assertThat(captured.getIsPrivate()).isTrue();
        Assertions.assertThat(captured.getRoomToken()).isNotBlank();
        Assertions.assertThat(captured.getName()).isEqualTo(room.getName());
        Assertions.assertThat(captured.getOwnerName()).isEqualTo(ownerName);
        Assertions.assertThat(captured.getTags()).contains(movieTagEntity);
    }

    @Test
    void shouldCreatePublicRoom() {
        //given
        Room room = getRoom(false);
        room.setRoomToken(null);
        room.setTags(Set.of(
                new Tag("Movie")
        ));
        TagEntity movieTagEntity = new TagEntity("Movie", ZonedDateTime.now());
        String ownerName = faker.elderScrolls().firstName();
        Mockito.when(principal.getName()).thenReturn(ownerName);
        Mockito.when(tagRepository.findByName("Movie"))
                .thenReturn(Optional.of(movieTagEntity));

        //when
        roomService.createRoom(room, principal);

        //then
        Mockito.verify(roomRepository, Mockito.times(1)).save(roomCaptor.capture());
        Mockito.verify(roomValidator, Mockito.times(1)).validateRoomName(ArgumentMatchers.anyString());
        Mockito.verify(tagRepository, Mockito.times(1)).findByName(ArgumentMatchers.anyString());
        RoomEntity captured = roomCaptor.getValue();
        Assertions.assertThat(captured.getCreatedAt()).isNotNull();
        Assertions.assertThat(captured.getIsPrivate()).isFalse();
        Assertions.assertThat(captured.getRoomToken()).isNull();
        Assertions.assertThat(captured.getName()).isEqualTo(room.getName());
        Assertions.assertThat(captured.getOwnerName()).isEqualTo(ownerName);
        Assertions.assertThat(captured.getTags()).contains(movieTagEntity);
    }

    @Test
    void shouldReturnPrivateRoomById() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(true);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));
        Assertions.assertThatNoException().isThrownBy(
                () -> roomService.getRoomById(TestConstants.ROOM_ID, "token")
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
                () -> roomService.getRoomById(TestConstants.ROOM_ID, null)
        );

        Mockito.verify(roomRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
        Mockito.verify(roomValidator, Mockito.never())
                .validateToken(ArgumentMatchers.any(), ArgumentMatchers.anyString());
    }

    @Test
    void shouldThrowRoomNotFound() {
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> roomService.getRoomById(TestConstants.ROOM_ID, null)
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
                () -> roomService.getRoomByName("RoomName")
        ).isInstanceOf(RoomNotFoundException.class);

        Mockito.verify(roomRepository, Mockito.times(1)).findByName("RoomName");
    }

    @Test
    void shouldHandleDeleteRoomEvent() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(false);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));

        roomService.deleteRoom(TestConstants.ROOM_ID);

        Mockito.verify(roomDeleteEventHandler, Mockito.times(1))
                .handleRoomDeleteEvent(TestConstants.ROOM_ID);
    }

    @Test
    void shouldDeleteRoomByOwner() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(UUID.randomUUID());
        roomEntity.setIsPrivate(false);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));

        roomService.deleteRoomByOwner(roomEntity.getId().toString(), principal);

        Mockito.verify(roomRepository, Mockito.times(1)).findById(roomEntity.getId());
        Mockito.verify(roomValidator, Mockito.times(1)).validateOwner(ArgumentMatchers.any(), roomCaptor.capture());
        Mockito.verify(roomDeleteEventHandler, Mockito.times(1)).handleRoomDeleteEvent(roomEntity.getId().toString());

        RoomEntity captured = roomCaptor.getValue();
        Assertions.assertThat(captured.getId()).isEqualTo(roomEntity.getId());
    }

    @Test
    void shouldAddUserCountAndUpdateMaxCount() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setUserQnt(0);
        roomEntity.setMaxUserQnt(0);
        roomEntity.setId(UUID.randomUUID());
        roomEntity.setIsPrivate(false);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));

        roomService.addUserCount(roomEntity.getId().toString(), "Username");

        Mockito.verify(roomRepository, Mockito.times(1)).findById(roomEntity.getId());
        Mockito.verify(roomRepository, Mockito.times(1)).save(roomEntity);
        Assertions.assertThat(roomEntity.getUsers()).contains("Username");
        Assertions.assertThat(roomEntity.getUserQnt()).isEqualTo(1);
        Assertions.assertThat(roomEntity.getMaxUserQnt()).isEqualTo(1);
    }

    @Test
    void shouldDecreaseUserCount() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setUserQnt(2);
        roomEntity.setMaxUserQnt(2);
        roomEntity.setUsers(new HashSet<>(List.of("Username", "Username 2")));
        roomEntity.setId(UUID.randomUUID());
        roomEntity.setIsPrivate(false);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));

        roomService.decreaseUserCount(roomEntity.getId().toString(), "Username");

        Mockito.verify(roomRepository, Mockito.times(1)).findById(roomEntity.getId());
        Mockito.verify(roomRepository, Mockito.times(1)).save(roomEntity);
        Assertions.assertThat(roomEntity.getUsers()).doesNotContain("Username");
        Assertions.assertThat(roomEntity.getUserQnt()).isEqualTo(1);
        Assertions.assertThat(roomEntity.getMaxUserQnt()).isEqualTo(2);
    }

    @Test
    void shouldDoNothingIfNotRoomInRepoWhenConnectUser() {
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        roomService.addUserCount(UUID.randomUUID().toString(), "username");

        Mockito.verify(roomRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void shouldDoNothingIfNotRoomInRepoWhenDisconnectUser() {
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        roomService.decreaseUserCount(UUID.randomUUID().toString(), "username");

        Mockito.verify(roomRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    private Room getRoom(boolean isPrivate) {
        return Room.builder()
                .id(UUID.fromString(TestConstants.ROOM_ID))
                .name(faker.elderScrolls().city())
                .isPrivate(isPrivate)
                .activeUrl("https://youBube")
                .roomToken(faker.elderScrolls().region())
                .playerType(PlayerType.YOUTUBE).build();
    }

}
