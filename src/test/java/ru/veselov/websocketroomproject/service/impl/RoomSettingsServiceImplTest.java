package ru.veselov.websocketroomproject.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
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
        Assertions.assertThat(changed.getChangedAt()).isNotNull();
        Assertions.assertThat(changed.getOwnerName()).isNull();
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

}