package ru.veselov.websocketroomproject.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomSettingsService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RoomSettingsControllerTest {

    public static final String URL_PREFIX = "/api/v1/room/";

    WebTestClient webTestClient;

    Faker faker = new Faker();

    @Mock
    RoomSettingsService roomSettingsService;

    @InjectMocks
    RoomSettingController roomSettingController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(roomSettingController).build();
    }


    @Test
    void shouldConsumeRoomSettingsDTOAndReturnRoomFromService() {
        Room room = getRoom(false);
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder()
                .roomName("name").playerType(PlayerType.YOUTUBE).build();
        Mockito.when(roomSettingsService.changeSettings(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(room);
        WebTestClient.BodyContentSpec resultBody = webTestClient.put().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path(TestConstants.ROOM_ID).build())
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isAccepted().expectBody();

        validateReturnedRoomBody(resultBody, room);
        Mockito.verify(roomSettingsService, Mockito.times(1))
                .changeSettings(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void shouldAddUrlAndReturnUrl() {
        UrlDto urlDto = new UrlDto("https://hello.com");

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("url/" + TestConstants.ROOM_ID).build())
                .bodyValue(urlDto)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$.url").isEqualTo(urlDto.getUrl());

        Mockito.verify(roomSettingsService, Mockito.times(1)).addUrl(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

    private Room getRoom(boolean isPrivate) {
        return Room.builder()
                .id(UUID.fromString(TestConstants.ROOM_ID))
                .name(faker.elderScrolls().city())
                .isPrivate(isPrivate)
                .activeUrl("https://youBube")
                .roomToken(faker.elderScrolls().region())
                .ownerName(faker.elderScrolls().firstName())
                .playerType(PlayerType.YOUTUBE)
                .createdAt(ZonedDateTime.now())
                .changedAt(ZonedDateTime.now()).build();
    }

    private void validateReturnedRoomBody(WebTestClient.BodyContentSpec resultBody, Room room) {
        resultBody
                .jsonPath("$.ownerName").isEqualTo(room.getOwnerName())
                .jsonPath("$.name").isEqualTo(room.getName())
                .jsonPath("$.id").isEqualTo(room.getId().toString())
                .jsonPath("$.isPrivate").isEqualTo(room.getIsPrivate())
                .jsonPath("$.activeUrl").isEqualTo(room.getActiveUrl())
                .jsonPath("$.roomToken").isEqualTo(room.getRoomToken())
                .jsonPath("$.playerType").isEqualTo(room.getPlayerType().toString())
                .jsonPath("$.createdAt").isEqualTo(room.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")))
                .jsonPath("$.changedAt").isEqualTo(room.getChangedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")));
    }


}
