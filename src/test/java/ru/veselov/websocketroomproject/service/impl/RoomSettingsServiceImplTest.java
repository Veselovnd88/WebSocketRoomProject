package ru.veselov.websocketroomproject.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.validation.RoomValidator;

@ExtendWith(MockitoExtension.class)
class RoomSettingsServiceImplTest {

    @Mock
    RoomValidator roomValidator;

    @InjectMocks
    RoomSettingsServiceImpl roomSettingsService;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(roomSettingsService, "zoneId", "Europe/Moscow", String.class);
        //ReflectionTestUtils.setField(roomService, "roomMapper", new RoomMapperImpl(), RoomMapper.class);
    }

    @Test
    void shouldSetNewName() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("NewName").build();
        RoomEntity roomEntity = new RoomEntity();

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Mockito.verify(roomValidator, Mockito.times(1)).validateRoomName(settings.getRoomName());
        Assertions.assertThat(changed.getName()).isEqualTo("NewName");
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

    @Test
    void shouldSetNewOwnerNameAndRoomName() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("NewName").ownerName("Owner").build();
        RoomEntity roomEntity = new RoomEntity();

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Mockito.verify(roomValidator, Mockito.times(1)).validateRoomName(settings.getRoomName());
        Assertions.assertThat(changed.getName()).isEqualTo("NewName");
        Assertions.assertThat(changed.getChangedAt()).isNotNull();
        Assertions.assertThat(changed.getOwnerName()).isEqualTo("Owner");
    }

    @Test
    void shouldSetStatusToPrivateAndCreateToken() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().isPrivate(true).build();
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(false);

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Mockito.verify(roomValidator, Mockito.never()).validateRoomName(ArgumentMatchers.anyString());
        Assertions.assertThat(changed.getIsPrivate()).isTrue();
        Assertions.assertThat(changed.getRoomToken()).isNotNull();
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

    @Test
    void shouldSetStatusToPublicAndRemoveToken() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().isPrivate(false).build();
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(true);
        roomEntity.setRoomToken("token");

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Mockito.verify(roomValidator, Mockito.never()).validateRoomName(ArgumentMatchers.anyString());
        Assertions.assertThat(changed.getIsPrivate()).isFalse();
        Assertions.assertThat(changed.getRoomToken()).isNull();
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

    @Test
    void shouldSetPlayerType() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().playerType("RUTUBE").build();
        RoomEntity roomEntity = new RoomEntity();

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Mockito.verify(roomValidator, Mockito.never()).validateRoomName(ArgumentMatchers.anyString());
        Assertions.assertThat(changed.getPlayerType()).isEqualTo(PlayerType.RUTUBE);
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

    @Test
    void shouldSetPlayerTypeToYoutubeByDefault() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().playerType("UNKNOWNTUBE").build();
        RoomEntity roomEntity = new RoomEntity();

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Mockito.verify(roomValidator, Mockito.never()).validateRoomName(ArgumentMatchers.anyString());
        Assertions.assertThat(changed.getPlayerType()).isEqualTo(PlayerType.YOUTUBE);
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

    @Test
    void shouldChangeToken() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().changeToken(true).build();
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(true);

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Mockito.verify(roomValidator, Mockito.never()).validateRoomName(ArgumentMatchers.anyString());
        Assertions.assertThat(changed.getRoomToken()).isNotNull();
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

    @Test
    void shouldNotChangeToken() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().changeToken(true).build();
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setIsPrivate(false);

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Mockito.verify(roomValidator, Mockito.never()).validateRoomName(ArgumentMatchers.anyString());
        Assertions.assertThat(changed.getRoomToken()).isNull();
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

}