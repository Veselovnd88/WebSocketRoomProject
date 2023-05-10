package ru.veselov.websocketroomproject.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class RoomControllerTest {

    private final static String ROOM_ID = "ec1edd63-4080-480b-84cc-2faee587999f";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    RoomService roomService;

    @MockBean
    RoomRepository roomRepository;

    @Test
    void shouldCallRoomServiceWithoutTokenAndReturnOKStatus() {
        webTestClient.get().uri(
                        uriBuilder -> uriBuilder.path("api").path("/room").path("/" + ROOM_ID).build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk().returnResult(Room.class);
        Mockito.verify(roomService, Mockito.times(1)).getRoomById(ROOM_ID, null);
    }

    @Test
    void shouldCallRoomServiceWithTokenAndReturnOKStatus() {
        webTestClient.get().uri(
                        uriBuilder -> uriBuilder.path("api").path("/room").path("/" + ROOM_ID)
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

}