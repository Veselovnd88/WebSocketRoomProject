package ru.veselov.websocketroomproject.app;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
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
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class RoomSettingsControllerIntegrationTest extends PostgresContainersConfig {

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
    void shouldChangeTags() {
        //given
        tagRepository.save(new TagEntity("Java"));
        RoomEntity saved = saveRoomToRepo();
        RoomSettingsDTO roomSettingsDTO = RoomSettingsDTO.builder()
                .tags(Set.of(
                        new Tag("Java"),
                        new Tag("Movie"))
                )
                .build();
        Optional<TagEntity> cartoon = tagRepository.findByName("Cartoon");
        Assertions.assertThat(cartoon).isPresent();
        Assertions.assertThat(cartoon.get().getRooms()).hasSize(1);

        //when
        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path(saved.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(roomSettingsDTO)
                .exchange().expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.changedAt").exists()
                .jsonPath("$.tags.size()").isEqualTo(2)
                .jsonPath("$.tags").value(Matchers.containsInAnyOrder("Java", "Movie"));

        //then
        cartoon = tagRepository.findByName("Cartoon");
        Assertions.assertThat(cartoon).isPresent();
        Assertions.assertThat(cartoon.get().getRooms()).isEmpty();
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
