package ru.veselov.websocketroomproject.app;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.app.containers.PostgresContainersConfig;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
@SuppressWarnings("rawtypes")
class RoomControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/room/";

    public static final String MOVIE = "Movie";

    public static final String CARTOON = "Cartoon";

    public static final String ANIME = "Anime";

    public static final String JAVA = "Java";

    private static final String OWNER = "user1";

    Faker faker = new Faker();

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RoomService roomService;

    @BeforeEach
    void init() {
        tagRepository.deleteAll();
    }

    @AfterEach
    void clearAll() {
        roomRepository.deleteAll();
        tagRepository.deleteAll();
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
        fillRepoWithRooms();
        Room saved = roomService.getRoomByName("aaa");

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

    @Test
    void shouldDeleteRoomAndNotifyUserAboutDeletion() {
        fillRepoWithRooms();
        Room roomAaa = roomService.getRoomByName("aaa");
        //subscribe to SSE
        FluxExchangeResult<ServerSentEvent> fluxResult = webTestClient.get()
                .uri("/api/v1/room/event?roomId=" + roomAaa.getId())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(ServerSentEvent.class);

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("delete")
                        .path("/" + roomAaa.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isNoContent();

        Optional<RoomEntity> aaa = roomRepository.findByName("aaa");
        Assertions.assertThat(aaa).isNotPresent();
        //checks if new SSE received
        StepVerifier.create(fluxResult.getResponseBody()).expectSubscription()
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals("init");
                })
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.ROOM_DELETE.name());
                }).
                thenCancel().verify();
    }

    public void fillRepoWithRooms() {
        roomRepository.deleteAll();//clear
        tagRepository.deleteAll();
        tagRepository.save(new TagEntity(MOVIE));
        tagRepository.save(new TagEntity(CARTOON));
        tagRepository.save(new TagEntity(ANIME));
        tagRepository.save(new TagEntity(JAVA));
        Principal principal1 = Mockito.mock(Principal.class);
        Mockito.when(principal1.getName()).thenReturn("user1");
        Room room1 = Room.builder().playerType(PlayerType.YOUTUBE).name("aaa").isPrivate(false).ownerName("user1")
                .tags(Set.of(new Tag(MOVIE),
                        new Tag(CARTOON)))
                .build();
        roomService.createRoom(room1, principal1);

        Principal principal2 = Mockito.mock(Principal.class);
        Mockito.when(principal2.getName()).thenReturn("yyy");
        Room room2 = Room.builder().playerType(PlayerType.RUTUBE).name("bbb").isPrivate(false).ownerName("yyy")
                .tags(Set.of(new Tag(MOVIE),
                        new Tag(ANIME)))
                .build();
        roomService.createRoom(room2, principal2);

        Principal principal3 = Mockito.mock(Principal.class);
        Mockito.when(principal3.getName()).thenReturn("zzz");
        Room room3 = Room.builder().playerType(PlayerType.TWITCH).name("ccc").isPrivate(false).ownerName("zzz")
                .tags(Set.of(new Tag(ANIME),
                        new Tag(JAVA)))
                .build();
        roomService.createRoom(room3, principal3);
    }


    private void validateReturnedRoomBody(WebTestClient.BodyContentSpec resultBody, Room room) {
        resultBody
                .jsonPath("$.ownerName").isEqualTo(OWNER)
                .jsonPath("$.name").isEqualTo(room.getName())
                .jsonPath("$.id").exists()
                .jsonPath("$.playerType").isEqualTo(room.getPlayerType().toString())
                .jsonPath("$.createdAt").exists();
    }

}
