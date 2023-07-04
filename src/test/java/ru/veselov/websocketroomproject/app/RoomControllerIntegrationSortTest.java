package ru.veselov.websocketroomproject.app;

import org.hamcrest.Matchers;
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
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class RoomControllerIntegrationSortAndValidationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/room/";

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
    void shouldReturnRoomArrayPage0SortedByNameAscending() {
        //0 page, sorted by name, ascending
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("page", 0)
                        .queryParam("sort", "name")
                        .queryParam("order", "asc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().consumeWith(System.out::println)
                .jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("aaa");

    }

    @Test
    void shouldReturnRoomArrayPage0ByDefaultSortedByOwnerNameAscending() {
        //default page, sorting by ownerName, ascending
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "ownerName")
                        .queryParam("order", "asc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("aaa");
    }

    @Test
    void shouldReturnRoomArrayPage0ByDefaultSortedByCreatedAtDescendingByDefault() {
        //default page, sorting by createdAt, default order - descending
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "createdAt")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("ccc");
    }

    @Test
    void shouldReturnRoomArrayPage0ByDefaultSortedByCreatedAtByDefaultDescendingByDefault() {
        //default page, default sorting, default order
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("ccc");
    }

    @Test
    void shouldReturnRoomArrayPage0ByDefaultSortedByPlayerTypeAscending() {
        //default page, sorting by playerType, ascending
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "playerType")
                        .queryParam("order", "asc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("bbb");
    }

    @Test
    void shouldReturnRoomArrayPage0ByDefaultSortedByPlayerTypeDescendingByDefault() {
        //default page, sorting by playerType, default order
        webTestClient.get().
                uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "playerType")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk().expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name")
                .isEqualTo("aaa");
    }

    @Test
    void shouldReturnRoomArrayWithSortingPage0ByDefaultSortedByNameAscending() {
        //if we not pass page parameter it will set to 0 by default, sorted by name, ascending
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "name")
                        .queryParam("order", "asc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("aaa");
    }

    @Test
    void shouldReturnRoomArrayPage0ByDefaultSortedByNameDescendingByDefault() {
        //if we will not pass order parameter - it will be set to desc by default
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "name")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("ccc");
    }

    @Test
    void shouldReturnRoomArrayPage0ByDefaultSortedByOwnerNameDescendingByDefault() {
        //default page, sorting by ownerName, default order
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "ownerName")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("ccc");
    }

    @Test
    void shouldReturnRoomArrayWithTagMovie() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all").path("/Movie")
                        .queryParam("page", 0)
                        .queryParam("sort", "name")
                        .queryParam("order", "asc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[0].name").isEqualTo("aaa")
                .jsonPath("$[0].tags").value(Matchers.contains("Movie"));
    }

    @Test
    void shouldReturnRoomArrayWithTagProgramming() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all").path("/Java")
                        .queryParam("page", 0)
                        .queryParam("sort", "name")
                        .queryParam("order", "asc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("ccc")
                .jsonPath("$[0].tags").value(Matchers.contains("Java"));
    }

    public void fillRepoWithRooms() {
        roomRepository.deleteAll();//clear
        tagRepository.deleteAll();
        tagRepository.save(new TagEntity("Movie"));
        tagRepository.save(new TagEntity("Cartoon"));
        tagRepository.save(new TagEntity("Anime"));
        tagRepository.save(new TagEntity("Java"));
        Principal principal1 = Mockito.mock(Principal.class);
        Mockito.when(principal1.getName()).thenReturn("xxx");
        Room room1 = Room.builder().playerType(PlayerType.YOUTUBE).name("aaa").isPrivate(false).ownerName("xxx")
                .tags(Set.of(new Tag("Movie"),
                        new Tag("Cartoon")))
                .build();
        roomService.createRoom(room1, principal1);

        Principal principal2 = Mockito.mock(Principal.class);
        Mockito.when(principal2.getName()).thenReturn("yyy");
        Room room2 = Room.builder().playerType(PlayerType.RUTUBE).name("bbb").isPrivate(false).ownerName("yyy")
                .tags(Set.of(new Tag("Movie"),
                        new Tag("Anime")))
                .build();
        roomService.createRoom(room2, principal2);

        Principal principal3 = Mockito.mock(Principal.class);
        Mockito.when(principal3.getName()).thenReturn("zzz");
        Room room3 = Room.builder().playerType(PlayerType.TWITCH).name("ccc").isPrivate(false).ownerName("zzz")
                .tags(Set.of(new Tag("Anime"),
                        new Tag("Java")))
                .build();
        roomService.createRoom(room3, principal3);
    }

}
