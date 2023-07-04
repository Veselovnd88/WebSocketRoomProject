package ru.veselov.websocketroomproject.app;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
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
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@DirtiesContext
public class ErrorResponseIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/room/";

    private final static String ROOM_ID = "ec1edd63-4080-480b-84cc-2faee587999f";

    private static final String OWNER = "user1";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomService roomService;

    Faker faker = new Faker();

    @AfterEach
    void cleanUp() {
        roomRepository.deleteAll();
    }

    @Test
    void shouldReturnUnauthorizedResponseIfNoAuthorizationHeader() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .build())
                .exchange().expectStatus().isUnauthorized()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_UNAUTHORIZED.toString());
    }

    @Test
    void shouldReturnUnauthorizedResponseIfJwtIsInvalid() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, "Invalid Jwt Here"))
                .exchange().expectStatus().isUnauthorized()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_UNAUTHORIZED.toString());
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
                .tags(Set.of(new Tag("Other")))
                .isPrivate(true)
                .playerType(PlayerType.YOUTUBE).build();

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomToSave)
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT).expectBody()
                .jsonPath("$.error").isEqualTo(ErrorCode.ERROR_CONFLICT.toString());
    }

    @Test
    void shouldReturnValidationErrorWhenCreatingRoomWithoutName() {
        Room transferedRoom = Room.builder()
                .isPrivate(true)
                .tags(Set.of(new Tag("Other")))
                .playerType(PlayerType.YOUTUBE).build();

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(transferedRoom)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations").isArray()
                .jsonPath("$.violations[0].fieldName").isEqualTo("name");
    }

    @Test
    void shouldReturnValidationErrorWhenCreatingRoomWithoutTags() {
        Room transferedRoom = Room.builder()
                .isPrivate(true)
                .name("name")
                .playerType(PlayerType.YOUTUBE).build();

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(transferedRoom)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations").isArray()
                .jsonPath("$.violations[0].fieldName").isEqualTo("tags");
    }

    @ParameterizedTest
    @ValueSource(strings = {"aa", "moreThanThirtyCharacters26283032"})
    void shouldReturnValidationErrorWhenCreatingRoomWithIncorrectName(String name) {
        Room transferedRoom = Room.builder()
                .isPrivate(true)
                .name(name)
                .tags(Set.of(new Tag("Other")))
                .playerType(PlayerType.YOUTUBE).build();

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(transferedRoom)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations").isArray()
                .jsonPath("$.violations[0].fieldName").isEqualTo("name");
    }

    @Test
    void shouldReturnValidationErrorWhenCreatingRoomWithoutPlayer() {
        Room transferedRoom = Room.builder()
                .isPrivate(true)
                .tags(Set.of(new Tag("Other")))
                .name(faker.elderScrolls().lastName()).build();

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(transferedRoom)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations").isArray()
                .jsonPath("$.violations[0].fieldName").isEqualTo("playerType");
    }

    @Test
    void shouldReturnRoomNotFoundMessage() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(ROOM_ID)
                        .queryParam("token", "abc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnForbiddenMessageWhenTryingToGetPrivateRoomWithoutToken() {
        Room saved = savePrivateRoomToRepo();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_INVALID_ROOM_TOKEN.toString());
    }

    @Test
    void shouldReturnForbiddenMessageWhenTryingToGetPrivateRoomWithInvalidRoomToken() {
        Room saved = savePrivateRoomToRepo();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .queryParam("token", saved.getRoomToken() + "a")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_INVALID_ROOM_TOKEN.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"aa", "moreThanThirtyCharacters26283032"})
    void shouldReturnValidationErrorWhenNotCorrectRoomNameInSettings(String name) {
        Room saved = savePrivateRoomToRepo();
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder().roomName(name).build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations").isArray()
                .jsonPath("$.violations[0].fieldName").isEqualTo("roomName");
    }

    @Test
    void shouldReturnUnauthorizedErrorWhenNotOwnerTryingToApplyNewSetting() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setPlayerType(PlayerType.YOUTUBE);
        roomEntity.setName(faker.elderScrolls().region());
        roomEntity.setIsPrivate(true);
        roomEntity.setOwnerName("NOT OWNER");
        roomEntity.setRoomToken("abc");
        roomEntity.setCreatedAt(ZonedDateTime.now());
        RoomEntity saved = roomRepository.save(roomEntity);
        String updatedName = faker.elderScrolls().lastName();
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder().roomName(updatedName).build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isForbidden().expectBody()
                .jsonPath("$.error").isEqualTo(ErrorCode.ERROR_NOT_ROOM_OWNER.toString());
    }

    @Test
    void shouldReturnValidationErrorWithUrlField() {
        Room saved = savePrivateRoomToRepo();
        UrlDto urlDto = new UrlDto("NotUrl");

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/url/")
                        .path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(urlDto)
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("url");
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
    void shouldReturnValidationErrorWhenPassNotCorrectSortingOrder() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("page", 0)
                        .queryParam("sort", "name")
                        .queryParam("order", "not an order parameter")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("order");
    }

    @Test
    void shouldReturnValidationErrorWhenPassNegativePage() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("page", -1)
                        .queryParam("sort", "ownerName")
                        .queryParam("order", "desc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("page");
    }

    @Test
    void shouldReturnValidationErrorWhenPassNotCorrectSortField() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("page", 0)
                        .queryParam("sort", "Not a sort parameter")
                        .queryParam("order", "desc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("sort");
    }

    @Test
    void shouldReturnPageNumberExceedErrorFindAll() {
        savePrivateRoomToRepo();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("page", 500)
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_PAGE_NUM_EXCEED.toString());
    }

    @Test
    void shouldReturnPageNumberExceedErrorFindAllWithTag() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all").path("/Java")
                        .queryParam("page", 500)
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_PAGE_NUM_EXCEED.toString());
    }

    private Room savePrivateRoomToRepo() {
        Room room = Room.builder().playerType(PlayerType.YOUTUBE).name(faker.elderScrolls().region())
                .isPrivate(true).ownerName(OWNER).tags(Set.of(new Tag("Java")))
                .build();
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(OWNER);
        return roomService.createRoom(room, principal);
    }

}
