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
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    private final static String ROOM_ID = "ec1edd63-4080-480b-84cc-2faee587999f";

    WebTestClient webTestClient;

    Faker faker = new Faker();

    @Mock
    RoomService roomService;

    @Mock
    RoomRepository roomRepository;

    @InjectMocks
    RoomController roomController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(roomController).build();
    }

    @Test
    void shouldReturnCorrectRoomWithoutToken() {
        Room room = getRoom(true);
        Mockito.when(roomService.getRoomById(ROOM_ID, null)).thenReturn(room);
        WebTestClient.BodyContentSpec resultBody = webTestClient.get().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/" + ROOM_ID).build())
                .exchange().expectStatus().isOk().expectBody();
        validateReturnedRoomBody(resultBody, room);

        Mockito.verify(roomService, Mockito.times(1)).getRoomById(ROOM_ID, null);
    }

    @Test
    void shouldCallRoomServiceWithTokenAndReturnOKStatus() {
        webTestClient.get().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/" + ROOM_ID)
                                .queryParam("token", "secret").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk().returnResult(Room.class);
        Mockito.verify(roomService, Mockito.times(1)).getRoomById(ROOM_ID, "secret");
    }

    @Test
    void shouldCallRoomServiceAndReturnACCEPTEDStatus() {
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder().roomName("newName").playerType("Youtube").build();
        webTestClient.put().uri(
                        uriBuilder -> uriBuilder.path("api").path("/room").path("/" + ROOM_ID).build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isAccepted().returnResult(Room.class);
        Mockito.verify(roomService, Mockito.times(1))
                .changeSettings(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(RoomSettingsDTO.class),
                        ArgumentMatchers.any(Principal.class)
                );
    }

    @Test
    void shouldCreateRoomAndReturnCREATEDStatus() {
        Room room = Room.builder().isPrivate(true).name("Room").ownerName("user1").build();
        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("api").path("/room").path("/create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(room)
                .exchange().expectStatus().isCreated().returnResult(Room.class);

        Mockito.verify(roomService, Mockito.times(1)).createRoom(ArgumentMatchers.any(Room.class));
    }

    @Test
    void shouldAddUrlAndReturnACCEPTEDStatus() {
        UrlDto urlDto = new UrlDto("http://url");
        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("api").path("/room").path("/" + ROOM_ID).build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(urlDto)
                .exchange().expectStatus().isAccepted().returnResult(Room.class);

        Mockito.verify(roomService, Mockito.times(1)).addUrl(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any());
    }

    @Test
    void shouldReturn403Status() {
        webTestClient.get().uri(
                        uriBuilder -> uriBuilder.path("api").path("/room").path("/" + ROOM_ID).build())
                .exchange().expectStatus().is4xxClientError();
    }

    private Room getRoom(boolean isPrivate) {
        return new Room(
                UUID.fromString(ROOM_ID),
                faker.elderScrolls().city(),
                isPrivate,
                "anyUrl",
                faker.elderScrolls().region(),
                faker.elderScrolls().firstName(),
                PlayerType.YOUTUBE,
                ZonedDateTime.now(),
                ZonedDateTime.now()
        );
    }

    private void validateReturnedRoomBody(WebTestClient.BodyContentSpec resultBody, Room room) {
        resultBody
                .jsonPath("$.ownerName").isEqualTo(room.getOwnerName())
                .jsonPath("$.name").isEqualTo(room.getName())
                .jsonPath("$.id").isEqualTo(room.getId())//TODO doesnt work
                .jsonPath("$.isPrivate").isEqualTo(room.getIsPrivate())
                .jsonPath("$.activeUrl").isEqualTo(room.getActiveUrl())
                .jsonPath("$.roomToken").isEqualTo(room.getRoomToken())
                .jsonPath("$.playerType").isEqualTo(room.getPlayerType().toString())
                .jsonPath("$.createdAt").exists()
                .jsonPath("$.changedAt").exists();
    }

}