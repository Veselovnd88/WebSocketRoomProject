package ru.veselov.websocketroomproject.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.service.RoomSettingsService;

@SpringBootTest
class RoomSettingsServiceImplTest {

    @Autowired
    RoomSettingsService roomSettingsService;

    @Test
    void shouldSetNewName() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("NewName").build();
        RoomEntity roomEntity = new RoomEntity();

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

        Assertions.assertThat(changed.getName()).isEqualTo("NewName");
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

    @Test
    void shouldSetNewOwnerNameAndRoomName() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().roomName("NewName").ownerName("Owner").build();
        RoomEntity roomEntity = new RoomEntity();

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

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

        Assertions.assertThat(changed.getPlayerType()).isEqualTo(PlayerType.RUTUBE);
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

    @Test
    void shouldSetPlayerTypeToYoutubeByDefault() {
        RoomSettingsDTO settings = RoomSettingsDTO.builder().playerType("UNKNOWNTUBE").build();
        RoomEntity roomEntity = new RoomEntity();

        RoomEntity changed = roomSettingsService.applySettings(roomEntity, settings);

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

        Assertions.assertThat(changed.getRoomToken()).isNull();
        Assertions.assertThat(changed.getChangedAt()).isNotNull();//checked if new zdt was set
        Assertions.assertThat(changed.getOwnerName()).isNull(); //checked if field wasn't changed
    }

}