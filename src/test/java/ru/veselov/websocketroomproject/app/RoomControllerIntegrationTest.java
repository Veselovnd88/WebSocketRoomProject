package ru.veselov.websocketroomproject.app;

import net.datafaker.Faker;
import org.hamcrest.Matchers;
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
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.error.ErrorCode;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;

import java.time.ZonedDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class RoomControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/room/";

    private static final String OWNER = "user1";

    Faker faker = new Faker();

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    private RoomRepository roomRepository;

    @AfterEach
    void clearAll() {
        roomRepository.deleteAll();
    }

    @Test
    void shouldCreateAndReturnPrivateRoom() {
        Room roomToSave = Room.builder()
                .name(faker.elderScrolls().region())
                .isPrivate(true)
                .playerType(PlayerType.YOUTUBE).build();

        WebTestClient.BodyContentSpec resultBody = webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomToSave)
                .exchange().expectStatus().isCreated().expectBody()
                .jsonPath("$.roomToken").exists()
                .jsonPath("$.isPrivate").isEqualTo(true)
                .jsonPath("$.changedAt").doesNotExist();
        validateReturnedRoomBody(resultBody, roomToSave);
    }

    @Test
    void shouldCreateAndReturnPublicRoom() {
        Room roomToSave = Room.builder()
                .name(faker.elderScrolls().region())
                .playerType(PlayerType.RUTUBE).build();

        WebTestClient.BodyContentSpec resultBody = webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomToSave)
                .exchange().expectStatus().isCreated().expectBody()
                .jsonPath("$.roomToken").doesNotExist()
                .jsonPath("$.isPrivate").isEqualTo(false)
                .jsonPath("$.changedAt").doesNotExist();
        validateReturnedRoomBody(resultBody, roomToSave);
    }

    @Test
    void shouldReturnRoomById() {
        RoomEntity saved = saveRoomToRepo();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .queryParam("token", "abc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.ownerName").isEqualTo(saved.getOwnerName())
                .jsonPath("$.name").isEqualTo(saved.getName())
                .jsonPath("$.id").isEqualTo(saved.getId().toString())
                .jsonPath("$.isPrivate").isEqualTo(saved.getIsPrivate())
                .jsonPath("$.playerType").isEqualTo(saved.getPlayerType().toString())
                .jsonPath("$.createdAt").exists();
    }


    @Test
    void shouldReturnValidationErrorWhenRoomIdIsNotUUID() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("NotUUID")
                        .queryParam("token", "abc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations").isArray()
                .jsonPath("$.violations[0].fieldName").isEqualTo("id");
    }

    @Test
    void shouldChangeRoomNameAndReturnUpdatedRoom() {
        RoomEntity saved = saveRoomToRepo();
        String updatedName = faker.elderScrolls().lastName();
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder().roomName(updatedName).build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$.name").isEqualTo(updatedName)
                .jsonPath("$.changedAt").exists()
                .jsonPath("$.isPrivate").isEqualTo(saved.getIsPrivate());
    }

    @Test
    void shouldChangeRoomOwnerAndReturnUpdatedRoom() {
        RoomEntity saved = saveRoomToRepo();
        String updatedOwnerName = faker.elderScrolls().lastName();
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder().ownerName(updatedOwnerName).build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$.ownerName").isEqualTo(updatedOwnerName)
                .jsonPath("$.changedAt").exists()
                .jsonPath("$.isPrivate").isEqualTo(saved.getIsPrivate());
    }

    @Test
    void shouldChangeRoomStatusAndReturnUpdatedRoom() {
        RoomEntity saved = saveRoomToRepo();
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder().isPrivate(false).build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$.ownerName").isEqualTo(saved.getOwnerName())
                .jsonPath("$.changedAt").exists()
                .jsonPath("$.isPrivate").isEqualTo(false)
                .jsonPath("$.roomToken").doesNotExist();
    }

    @Test
    void shouldChangeRoomTokenAndReturnUpdatedRoom() {
        RoomEntity saved = saveRoomToRepo();
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder().changeToken(true).build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$.ownerName").isEqualTo(saved.getOwnerName())
                .jsonPath("$.changedAt").exists()
                .jsonPath("$.isPrivate").isEqualTo(true)
                .jsonPath("$.roomToken").value(Matchers.not(saved.getRoomToken()));
    }

    @Test
    void shouldAddNewUrl() {
        RoomEntity saved = saveRoomToRepo();
        UrlDto urlDto = new UrlDto("https://url.ru");

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/url/")
                        .path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(urlDto)
                .exchange().expectStatus().isAccepted().expectBody()
                .jsonPath("$.url").isEqualTo(urlDto.getUrl());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .queryParam("token", saved.getRoomToken())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectBody().jsonPath("$.activeUrl").isEqualTo(urlDto.getUrl());
    }

    private void validateReturnedRoomBody(WebTestClient.BodyContentSpec resultBody, Room room) {
        resultBody
                .jsonPath("$.ownerName").isEqualTo(OWNER)
                .jsonPath("$.name").isEqualTo(room.getName())
                .jsonPath("$.id").exists()
                .jsonPath("$.playerType").isEqualTo(room.getPlayerType().toString())
                .jsonPath("$.createdAt").exists();
    }

    private RoomEntity saveRoomToRepo() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setPlayerType(PlayerType.YOUTUBE);
        roomEntity.setName(faker.elderScrolls().region());
        roomEntity.setIsPrivate(true);
        roomEntity.setOwnerName(OWNER);
        roomEntity.setRoomToken("abc");
        roomEntity.setCreatedAt(ZonedDateTime.now());
        return roomRepository.save(roomEntity);
    }


}
