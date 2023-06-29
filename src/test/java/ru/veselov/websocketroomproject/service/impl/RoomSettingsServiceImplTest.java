package ru.veselov.websocketroomproject.service.impl;

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
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.entity.UrlEntity;
import ru.veselov.websocketroomproject.event.handler.impl.RoomUpdateHandlerImpl;
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
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RoomSettingsServiceImplTest {

    @Mock
    RoomValidator roomValidator;

    @Mock
    RoomRepository roomRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    RoomUpdateHandlerImpl roomUpdateHandler;

    @Mock
    Principal principal;

    @InjectMocks
    RoomSettingsServiceImpl roomSettingsService;

    @Captor
    ArgumentCaptor<RoomEntity> roomCaptor;

    @BeforeEach
    public void init() {
        RoomMapperImpl roomMapper = new RoomMapperImpl();
        ReflectionTestUtils.setField(roomSettingsService, "zoneId", "Europe/Moscow", String.class);
        ReflectionTestUtils.setField(roomSettingsService, "roomMapper", roomMapper, RoomMapper.class);
        ReflectionTestUtils.setField(roomMapper, "tagMapper", new TagMapperImpl(), TagMapper.class);
    }

    @Test
    void shouldChangeRoomNameAndPlayerType() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("rename").playerType(PlayerType.YOUTUBE).build();
        RoomEntity roomEntity = new RoomEntity();
        setUpRoomRepo(roomEntity);

        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getName()).isEqualTo(settings.getRoomName());
        Assertions.assertThat(room.getPlayerType()).isEqualTo(PlayerType.YOUTUBE);
    }

    @Test
    void shouldChangeName() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("NewName").build();
        RoomEntity roomEntity = new RoomEntity();
        setUpRoomRepo(roomEntity);

        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getName()).isEqualTo(settings.getRoomName());
    }

    @Test
    void shouldChangeRoomOwnerAndRoomName() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("NewName").ownerName("Owner").build();
        RoomEntity roomEntity = new RoomEntity();
        setUpRoomRepo(roomEntity);

        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getName()).isEqualTo(settings.getRoomName());
        Assertions.assertThat(room.getOwnerName()).isEqualTo(settings.getOwnerName());
    }

    @Test
    void shouldChangeStatusAndCreateToken() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().isPrivate(true).build();
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(false);
        setUpRoomRepo(roomEntity);

        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getIsPrivate()).isEqualTo(settings.getIsPrivate());
        Assertions.assertThat(room.getRoomToken()).isNotNull();
    }

    @Test
    void shouldChangeStatusToPublicAndRemoveToken() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().isPrivate(false).build();
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(true);
        roomEntity.setRoomToken("token");
        setUpRoomRepo(roomEntity);

        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getIsPrivate()).isFalse();
        Assertions.assertThat(room.getRoomToken()).isNull();
    }

    @Test
    void shouldChangePlayerType() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().playerType(PlayerType.RUTUBE).build();
        RoomEntity roomEntity = new RoomEntity();
        setUpRoomRepo(roomEntity);

        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getPlayerType()).isEqualTo(settings.getPlayerType());
    }

    @Test
    void shouldChangeToken() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().changeToken(true).build();
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(true);
        setUpRoomRepo(roomEntity);

        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getRoomToken()).isNotNull();//it was null before setting
    }

    @Test
    void shouldNotChangeToken() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().changeToken(true).build();
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(false);
        setUpRoomRepo(roomEntity);

        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getRoomToken()).isNull();
    }

    @Test
    void shouldChangeTags() {
        //given
        RoomSettingsDTO settings = RoomSettingsDTO.builder().tags(Set.of(
                new Tag("Movie"),
                new Tag("Comedy"),
                new Tag("Java")
        )).build();
        RoomEntity roomEntity = new RoomEntity();
        Set<TagEntity> tagEntities = new HashSet<>();
        TagEntity movie = new TagEntity("Movie");
        TagEntity spring = new TagEntity("Spring");
        TagEntity animals = new TagEntity("Animals");
        tagEntities.add(movie);
        tagEntities.add(spring);
        tagEntities.add(animals);
        roomEntity.setTags(tagEntities);
        setUpRoomRepo(roomEntity);
        Mockito.when(tagRepository.findByName(ArgumentMatchers.anyString())).thenAnswer(
                invocation -> {
                    String argument = invocation.getArgument(0, String.class);
                    if ("Movie".equals(argument)) {
                        return Optional.of(movie);
                    }
                    if ("Spring".equals(argument)) {
                        return Optional.of(spring);
                    }
                    if ("Animals".equals(argument)) {
                        return Optional.of(animals);
                    }
                    if ("Comedy".equals(argument)) {
                        return Optional.of(new TagEntity("Comedy"));
                    }
                    if ("Java".equals(argument)) {
                        return Optional.of(new TagEntity("Java"));
                    }
                    return Optional.empty();
                }
        );

        //when
        Room room = roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal);

        //then
        checkCallRoomRepoAndValidatorAndUpdateHandler();
        checkChangedAtTime(room);
        Assertions.assertThat(room.getTags()).hasSize(3)
                .allMatch(x -> Set.of("Java", "Movie", "Comedy").contains(x.getName()));
    }

    @Test
    void shouldThrowRoomNotFoundExceptionIfRoomNotExisting() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("rename").playerType(PlayerType.YOUTUBE).build();
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> roomSettingsService.changeSettings(TestConstants.ROOM_ID, settings, principal)
        ).isInstanceOf(RoomNotFoundException.class);

        Mockito.verify(roomRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
        Mockito.verify(roomValidator, Mockito.never()).validateOwner(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void shouldAddUrlToRoom() {
        String url = "https://i-am-pretty-url.com";
        UrlEntity urlEntity = new UrlEntity(url, ZonedDateTime.now());
        RoomEntity roomEntity = new RoomEntity();
        setUpRoomRepo(roomEntity);

        Assertions.assertThatNoException().isThrownBy(
                () -> roomSettingsService.addUrl(TestConstants.ROOM_ID, url, principal)
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
        String url = "https://i-am-pretty-url.com";
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> roomSettingsService.addUrl(TestConstants.ROOM_ID, url, principal)
        ).isInstanceOf(RoomNotFoundException.class);

        Mockito.verify(roomRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    private void setUpRoomRepo(RoomEntity roomEntity) {
        Mockito.when(roomRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(roomEntity));
        Mockito.when(roomRepository.save(ArgumentMatchers.any())).thenReturn(roomEntity);
    }

    private void checkCallRoomRepoAndValidatorAndUpdateHandler() {
        Mockito.verify(roomRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
        Mockito.verify(roomValidator, Mockito.times(1)).validateOwner(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(roomUpdateHandler, Mockito.times(1)).handleRoomSettingUpdateEvent(ArgumentMatchers.any());
    }

    private void checkChangedAtTime(Room room) {
        Assertions.assertThat(room.getChangedAt()).isNotNull();
    }

}
