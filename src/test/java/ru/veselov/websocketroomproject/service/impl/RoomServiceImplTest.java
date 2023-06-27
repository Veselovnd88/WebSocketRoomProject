package ru.veselov.websocketroomproject.service.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.entity.UrlEntity;
import ru.veselov.websocketroomproject.event.handler.impl.RoomUpdateHandlerImpl;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.mapper.RoomMapperImpl;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;
import ru.veselov.websocketroomproject.service.RoomSettingsService;
import ru.veselov.websocketroomproject.validation.RoomValidator;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    private final static String ROOM_ID = "ec1edd63-4080-480b-84cc-2faee587999f";

    Faker faker = new Faker();

    @Mock
    RoomRepository roomRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    RoomValidator roomValidator;

    @Mock
    RoomSettingsService roomSettingsService;

    @Mock
    Principal principal;

    @Mock
    RoomUpdateHandlerImpl roomUpdateHandler;

    @InjectMocks
    RoomServiceImpl roomService;

    @Captor
    ArgumentCaptor<RoomEntity> roomCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(roomService, "zoneId", "Europe/Moscow", String.class);
        ReflectionTestUtils.setField(roomService, "zone", ZoneId.of("Europe/Moscow"), ZoneId.class);
        ReflectionTestUtils.setField(roomService, "roomMapper", new RoomMapperImpl(), RoomMapper.class);
    }

    @Test
    void shouldCreatePrivateRoomWithTags() {
        //given
        Room room = getRoom(true);
        room.setTags(Set.of(
                new Tag("Movie"),
                new Tag("Stupid Video")
        ));
        TagEntity movieTagEntity = new TagEntity("Movie", ZonedDateTime.now());
        TagEntity stupidVideotagEntity = new TagEntity("Stupid Video", ZonedDateTime.now());
        String ownerName = faker.elderScrolls().firstName();
        Mockito.when(principal.getName()).thenReturn(ownerName);
        Mockito.when(tagRepository.findByName("Movie"))
                .thenReturn(Optional.of(movieTagEntity));
        Mockito.when(tagRepository.findByName("Stupid Video"))//FIXME
                .thenReturn(Optional.of(stupidVideotagEntity));

        //when
        roomService.createRoom(room, principal);

        //then
        Mockito.verify(roomRepository, Mockito.times(1)).save(roomCaptor.capture());
        Mockito.verify(roomValidator, Mockito.times(1)).validateRoomName(ArgumentMatchers.anyString());
        Mockito.verify(tagRepository, Mockito.times(2)).findByName(ArgumentMatchers.anyString());
        RoomEntity captured = roomCaptor.getValue();
        Assertions.assertThat(captured.getCreatedAt()).isNotNull();
        Assertions.assertThat(captured.getIsPrivate()).isTrue();
        Assertions.assertThat(captured.getRoomToken()).isNotBlank();
        Assertions.assertThat(captured.getName()).isEqualTo(room.getName());
        Assertions.assertThat(captured.getOwnerName()).isEqualTo(ownerName);
        Assertions.assertThat(captured.getTags()).contains(movieTagEntity).contains(stupidVideotagEntity);
    }

    @Test
    void shouldCreatePublicRoom() {
        //given
        Room room = getRoom(false);
        room.setRoomToken(null);
        room.setTags(Set.of(
                new Tag("Movie"),
                new Tag("Stupid Video")
        ));
        TagEntity movieTagEntity = new TagEntity("Movie", ZonedDateTime.now());
        TagEntity stupidVideotagEntity = new TagEntity("Stupid Video", ZonedDateTime.now());
        String ownerName = faker.elderScrolls().firstName();
        Mockito.when(principal.getName()).thenReturn(ownerName);
        Mockito.when(tagRepository.findByName("Movie"))
                .thenReturn(Optional.of(movieTagEntity));
        Mockito.when(tagRepository.findByName("Stupid Video"))
                .thenReturn(Optional.of(stupidVideotagEntity));

        //when
        roomService.createRoom(room, principal);

        //then
        Mockito.verify(roomRepository, Mockito.times(1)).save(roomCaptor.capture());
        Mockito.verify(roomValidator, Mockito.times(1)).validateRoomName(ArgumentMatchers.anyString());
        RoomEntity captured = roomCaptor.getValue();
        Assertions.assertThat(captured.getCreatedAt()).isNotNull();
        Assertions.assertThat(captured.getIsPrivate()).isFalse();
        Assertions.assertThat(captured.getRoomToken()).isNull();
        Assertions.assertThat(captured.getName()).isEqualTo(room.getName());
        Assertions.assertThat(captured.getOwnerName()).isEqualTo(ownerName);
        Assertions.assertThat(captured.getTags()).contains(movieTagEntity).contains(stupidVideotagEntity);
        Mockito.verify(tagRepository, Mockito.times(2)).findByName(ArgumentMatchers.anyString());
    }

    @Test
    void shouldReturnPrivateRoomById() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(true);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));
//FIXME
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
        //FIXME
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
                () -> roomService.getRoomByName("RoomName")
        ).isInstanceOf(RoomNotFoundException.class);

        Mockito.verify(roomRepository, Mockito.times(1)).findByName("RoomName");
    }

    @Test
    void shouldSaveNewRoomSettings() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("rename").playerType(PlayerType.YOUTUBE).build();
        RoomEntity roomEntity = new RoomEntity();
        RoomEntity roomWithChangedSettings = new RoomEntity();
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));
        Mockito.when(roomSettingsService.applySettings(roomEntity, settings)).thenReturn(roomWithChangedSettings);

        roomService.changeSettings(ROOM_ID, settings, principal);

        Mockito.verify(roomRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
        Mockito.verify(roomValidator, Mockito.times(1)).validateOwner(principal, roomEntity);
        Mockito.verify(roomSettingsService, Mockito.times(1)).applySettings(roomEntity, settings);
        Mockito.verify(roomUpdateHandler, Mockito.times(1)).handleRoomSettingUpdateEvent(ArgumentMatchers.any());
    }

    @Test
    void shouldThrowRoomNotFoundExceptionIfRoomNotExisting() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("rename").playerType(PlayerType.YOUTUBE).build();
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> roomService.changeSettings(ROOM_ID, settings, principal)
        ).isInstanceOf(RoomNotFoundException.class);

        Mockito.verify(roomRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
        Mockito.verify(roomValidator, Mockito.never()).validateOwner(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(roomSettingsService, Mockito.never())
                .applySettings(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void shouldAddUrlToRoom() {
        Principal principal = Mockito.mock(Principal.class);
        String url = "https://i-am-pretty-url.com";
        UrlEntity urlEntity = new UrlEntity(url, ZonedDateTime.now());
        RoomEntity roomEntity = new RoomEntity();
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));

        Assertions.assertThatNoException().isThrownBy(
                () -> roomService.addUrl(ROOM_ID, url, principal)
        );
        Mockito.verify(roomRepository, Mockito.times(1)).save(roomCaptor.capture());
        RoomEntity captured = roomCaptor.getValue();
        Assertions.assertThat(captured.getUrls()).hasSize(1);
        Assertions.assertThat(captured.getUrls().get(0).getUrl()).isEqualTo(urlEntity.getUrl());
        Assertions.assertThat(captured.getActiveUrl()).isEqualTo(url);
        Mockito.verify(roomUpdateHandler, Mockito.times(1))
                .handleActiveURLUpdateEvent(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    void shouldThrowExceptionIfNoRoomWhenWantToAddUrl() {
        Principal principal = Mockito.mock(Principal.class);
        String url = "https://i-am-pretty-url.com";
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> roomService.addUrl(ROOM_ID, url, principal)
        ).isInstanceOf(RoomNotFoundException.class);
        Mockito.verify(roomRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    private Room getRoom(boolean isPrivate) {
        return Room.builder()
                .id(UUID.fromString(ROOM_ID))
                .name(faker.elderScrolls().city())
                .isPrivate(isPrivate)
                .activeUrl("https://youBube")
                .roomToken(faker.elderScrolls().region())
                .playerType(PlayerType.YOUTUBE).build();
    }

}
