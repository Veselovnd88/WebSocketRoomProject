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
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    private final static String ROOM_ID = "ec1edd63-4080-480b-84cc-2faee587999f";

    WebTestClient webTestClient;

    Faker faker = new Faker();

    @Mock
    RoomService roomService;

    @InjectMocks
    RoomController roomController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(roomController).build();
    }

    @Test
    void shouldReturnRoomByRequestWithIdAndWithoutToken() {
        Room room = getRoom(true);
        Mockito.when(roomService.getRoomById(ROOM_ID, null)).thenReturn(room);
        WebTestClient.BodyContentSpec resultBody = webTestClient.get().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/" + ROOM_ID).build())
                .exchange().expectStatus().isOk().expectBody();
        validateReturnedRoomBody(resultBody, room);

        Mockito.verify(roomService, Mockito.times(1)).getRoomById(ROOM_ID, null);
    }

    @Test
    void shouldReturnRoomByRequestWithIdAndWithToken() {
        Room room = getRoom(false);
        Mockito.when(roomService.getRoomById(ROOM_ID, "secret")).thenReturn(room);

        WebTestClient.BodyContentSpec resultBody = webTestClient.get().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/" + ROOM_ID)
                                .queryParam("token", "secret").build())
                .exchange().expectStatus().isOk().expectBody();
        validateReturnedRoomBody(resultBody, room);

        Mockito.verify(roomService, Mockito.times(1)).getRoomById(ROOM_ID, "secret");
    }

    @Test
    void shouldConsumeRoomSettingsDTOAndReturnRoomFromService() {
        Room room = getRoom(false);
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder().id(ROOM_ID)
                .roomName("name").playerType(PlayerType.YOUTUBE).build();
        Mockito.when(roomService.changeSettings(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(room);
        WebTestClient.BodyContentSpec resultBody = webTestClient.put().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/" + ROOM_ID).build())
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isAccepted().expectBody();

        validateReturnedRoomBody(resultBody, room);
        Mockito.verify(roomService, Mockito.times(1))
                .changeSettings(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void shouldConsumeRoomAndReturnCreatedRoom() {
        Room savedRoom = getRoom(true);
        Room transferedRoom = Room.builder()
                .name(savedRoom.getName())
                .ownerName(savedRoom.getOwnerName())
                .playerType(PlayerType.YOUTUBE)
                .isPrivate(true).build();
        Mockito.when(roomService.createRoom(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(savedRoom);
        WebTestClient.BodyContentSpec resultBody = webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/create").build())
                .bodyValue(transferedRoom)
                .exchange().expectStatus().isCreated().expectBody();

        validateReturnedRoomBody(resultBody, savedRoom);
        Mockito.verify(roomService, Mockito.times(1)).createRoom(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void shouldAddUrlAndReturnUrl() {
        UrlDto urlDto = new UrlDto("http://hello.com");

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/url/" + ROOM_ID).build())
                .bodyValue(urlDto)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$.url").isEqualTo(urlDto.getUrl());

        Mockito.verify(roomService, Mockito.times(1)).addUrl(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

    private Room getRoom(boolean isPrivate) {
        return Room.builder()
                .id(UUID.fromString(ROOM_ID))
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