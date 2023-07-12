package ru.veselov.websocketroomproject.app;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class AdminControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/admin/";

    public static final String MOVIE = "Movie";

    public static final String CARTOON = "Cartoon";

    public static final String ANIME = "Anime";

    public static final String JAVA = "Java";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        fillRepoWithRooms();
    }

    @AfterEach
    void clear() {
        roomRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    void shouldDeleteTagMovieAndDeleteThisTagForEveryRoom() {
        //before test there is 3 rooms, and 2 with MOVIE tag
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("delete")
                        .path("/tag")
                        .path("/" + MOVIE)
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.size()").isEqualTo(3);

        List<Room> roomWithMovieTag = roomService.findAllByTag(MOVIE, 0, "name", "asc");
        Assertions.assertThat(roomWithMovieTag).isEmpty();
    }

    @Test
    void shouldAddNewTag() {
        Tag tag = new Tag("TestTag");

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("add")
                        .path("/tag")
                        .build()).bodyValue(tag)
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isCreated()
                .expectBody().jsonPath("$.size()").isEqualTo(5);

        Optional<TagEntity> testTag = tagRepository.findByName("TestTag");
        Assertions.assertThat(testTag).isPresent();
    }

    @Test
    void shouldDeleteRoomAndNotifyUserAboutDeletion() {
        Room roomAaa = roomService.getRoomByName("aaa");

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("delete")
                        .path("/room")
                        .path("/" + roomAaa.getId().toString())
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isNoContent();

        Optional<RoomEntity> aaa = roomRepository.findByName("aaa");
        Assertions.assertThat(aaa).isNotPresent();
    }

    public void fillRepoWithRooms() {
        roomRepository.deleteAll();//clear
        tagRepository.deleteAll();
        tagRepository.save(new TagEntity(MOVIE));
        tagRepository.save(new TagEntity(CARTOON));
        tagRepository.save(new TagEntity(ANIME));
        tagRepository.save(new TagEntity(JAVA));
        Principal principal1 = Mockito.mock(Principal.class);
        Mockito.when(principal1.getName()).thenReturn("xxx");
        Room room1 = Room.builder().playerType(PlayerType.YOUTUBE).name("aaa").isPrivate(false).ownerName("xxx")
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
}
