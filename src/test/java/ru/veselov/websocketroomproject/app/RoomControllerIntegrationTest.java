package ru.veselov.websocketroomproject.app;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.app.containers.PostgresContainersConfig;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.error.ErrorConstants;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;

import java.time.ZonedDateTime;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class RoomControllerIntegrationTest extends PostgresContainersConfig {

    Faker faker = new Faker();

    private final static String ROOM_ID = "ec1edd63-4080-480b-84cc-2faee587999f";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    private RoomRepository roomRepository;

    @AfterEach
    void clearAll() {
        roomRepository.deleteAll();
    }


    @Test
    void shouldCreateAndReturnRoom() {
        Room roomToSave = Room.builder()
                .name(faker.elderScrolls().region())
                .isPrivate(true)
                .playerType(PlayerType.YOUTUBE).build();

        WebTestClient.BodyContentSpec resultBody = webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomToSave)
                .exchange().expectStatus().isCreated().expectBody()
                .jsonPath("$.roomToken").exists()
                .jsonPath("$.changedAt").doesNotExist();
        validateReturnedRoomBody(resultBody, roomToSave);
    }

    @Test
    void shouldReturnRoomAlreadyExistsMessage() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setPlayerType(PlayerType.YOUTUBE);
        roomEntity.setName(faker.elderScrolls().region());
        roomEntity.setIsPrivate(true);
        roomEntity.setOwnerName(faker.elderScrolls().lastName());
        roomEntity.setRoomToken("abc");
        roomEntity.setCreatedAt(ZonedDateTime.now());
        roomRepository.save(roomEntity);
        Room roomToSave = Room.builder()
                .name(roomEntity.getName())
                .isPrivate(true)
                .playerType(PlayerType.YOUTUBE).build();

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomToSave)
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT).expectBody()
                .jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_CONFLICT);
    }

    @Test
    void shouldReturnValidatedErrorWhenCreateRoom() {
        Room transferedRoom = Room.builder()
                .isPrivate(true)
                .playerType(PlayerType.YOUTUBE).build();

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(transferedRoom)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_VALIDATION)
                .jsonPath("$.violations").exists();
    }

    @Test
    void shouldReturnRoom() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setPlayerType(PlayerType.YOUTUBE);
        roomEntity.setName(faker.elderScrolls().region());
        roomEntity.setIsPrivate(true);
        roomEntity.setOwnerName(faker.elderScrolls().lastName());
        roomEntity.setRoomToken("abc");
        roomEntity.setCreatedAt(ZonedDateTime.now());
        RoomEntity saved = roomRepository.save(roomEntity);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api").path("/room/").path(saved.getId().toString())
                        .queryParam("token", "abc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.ownerName").isEqualTo(roomEntity.getOwnerName())
                .jsonPath("$.name").isEqualTo(roomEntity.getName())
                .jsonPath("$.id").isEqualTo(saved.getId().toString())
                .jsonPath("$.isPrivate").isEqualTo(roomEntity.getIsPrivate())
                .jsonPath("$.playerType").isEqualTo(roomEntity.getPlayerType().toString())
                .jsonPath("$.createdAt").exists();
    }

    @Test
    void shouldReturnRoomNotFoundMessage() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api").path("/room/").path(ROOM_ID)
                        .queryParam("token", "abc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_NOT_FOUND);
    }

    @Test
    void shouldReturnUnauthorizedMessageWhenTryingToGetPrivateRoomWithoutToken() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api").path("/room/").path(ROOM_ID)
                        .queryParam("token", "abc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_NOT_FOUND);
    }


    private void validateReturnedRoomBody(WebTestClient.BodyContentSpec resultBody, Room room) {
        resultBody
                .jsonPath("$.ownerName").isEqualTo("user1")
                .jsonPath("$.name").isEqualTo(room.getName())
                .jsonPath("$.id").exists()
                .jsonPath("$.isPrivate").isEqualTo(room.getIsPrivate())
                .jsonPath("$.playerType").isEqualTo(room.getPlayerType().toString())
                .jsonPath("$.createdAt").exists();

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
}
