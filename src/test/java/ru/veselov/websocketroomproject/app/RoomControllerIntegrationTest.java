package ru.veselov.websocketroomproject.app;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.app.containers.PostgresContainersConfig;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;

import java.time.ZonedDateTime;
import java.util.Set;

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

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void init() {
        tagRepository.deleteAll();
    }

    @AfterEach
    void clearAll() {
        tagRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    void shouldCreateAndReturnPrivateRoom() {
        tagRepository.save(new TagEntity("Movie", ZonedDateTime.now()));
        tagRepository.save(new TagEntity("Cartoon", ZonedDateTime.now()));
        Room roomToSave = Room.builder()
                .name(faker.elderScrolls().region())
                .isPrivate(true)
                .tags(Set.of(
                        new Tag("Movie"),
                        new Tag("Cartoon")))
                .playerType(PlayerType.YOUTUBE).build();

        WebTestClient.BodyContentSpec resultBody = webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomToSave)
                .exchange().expectStatus().isCreated().expectBody()
                .jsonPath("$.roomToken").exists()
                .jsonPath("$.isPrivate").isEqualTo(true)
                .jsonPath("$.changedAt").doesNotExist()
                .jsonPath("$.tags").isArray()
                .jsonPath("$.tags.size()").isEqualTo(2);
        validateReturnedRoomBody(resultBody, roomToSave);
    }

    @Test
    void shouldCreateAndReturnPublicRoom() {
        tagRepository.save(new TagEntity("Other", ZonedDateTime.now()));
        Room roomToSave = Room.builder()
                .name(faker.elderScrolls().region())
                .tags(Set.of(new Tag("Other")))
                .playerType(PlayerType.RUTUBE).build();

        WebTestClient.BodyContentSpec resultBody = webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path(URL_PREFIX).path("create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomToSave)
                .exchange().expectStatus().isCreated().expectBody()
                .jsonPath("$.roomToken").doesNotExist()
                .jsonPath("$.isPrivate").isEqualTo(false)
                .jsonPath("$.changedAt").doesNotExist()
                .jsonPath("$.tags").isArray()
                .jsonPath("$.tags.size()").isEqualTo(1);
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
                .jsonPath("$.createdAt").exists()
                .jsonPath("$.tags").isArray()
                .jsonPath("$.tags.size()").isEqualTo(2);
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
        roomEntity.addTag(new TagEntity("Movie", ZonedDateTime.now()));
        roomEntity.addTag(new TagEntity("Cartoon", ZonedDateTime.now()));
        return roomRepository.save(roomEntity);
    }

}
