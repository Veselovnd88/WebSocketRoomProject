package ru.veselov.websocketroomproject.app;

import org.junit.jupiter.api.AfterEach;
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
import ru.veselov.websocketroomproject.exception.error.ErrorCode;
import ru.veselov.websocketroomproject.repository.RoomRepository;

import java.time.ZonedDateTime;

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

    @AfterEach
    void clearAll() {
        roomRepository.deleteAll();
    }

    @Test
    void shouldReturnArrayOfRoomWithSorting() {
        fillRepoWithRooms();
        //0 page, sorted by name, ascending
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("page", 0)
                        .queryParam("sort", "name")
                        .queryParam("order", "asc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("aaa");

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

        //if we will not pass order parameter - it will be set to desc by default
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "name")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("ccc");

        //default page, sorting by name, ascending
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "name")
                        .queryParam("order", "asc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("aaa");

        //default page, sorting by ownerName, default order
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "ownerName")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("ccc");

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

        //default page, sorting by createdAt, default order - descending
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "createdAt")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("ccc");

        //default page, default sorting, default order
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("ccc");

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

        //default page, sorting by playerType, default order
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("sort", "playerType")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("aaa");
    }

    @Test
    void shouldReturnValidationErrorWhenTryingPassNotCorrectSortParameters() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("page", -1)
                        .queryParam("sort", "ownerName")
                        .queryParam("order", "desc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("page");

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("all")
                        .queryParam("page", 0)
                        .queryParam("sort", "Not a sort parameter")
                        .queryParam("order", "desc")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("sort");

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

    private void fillRepoWithRooms() {
        roomRepository.deleteAll();
        RoomEntity room1 = new RoomEntity();
        room1.setPlayerType(PlayerType.YOUTUBE);
        room1.setName("aaa");
        room1.setIsPrivate(false);
        room1.setOwnerName("xxx");
        room1.setCreatedAt(ZonedDateTime.now());
        roomRepository.save(room1);
        RoomEntity room2 = new RoomEntity();
        room2.setPlayerType(PlayerType.RUTUBE);
        room2.setName("bbb");
        room2.setIsPrivate(false);
        room2.setOwnerName("yyy");
        room2.setCreatedAt(ZonedDateTime.now());
        roomRepository.save(room2);
        RoomEntity room3 = new RoomEntity();
        room3.setPlayerType(PlayerType.TWITCH);
        room3.setName("ccc");
        room3.setIsPrivate(false);
        room3.setOwnerName("zzz");
        room3.setCreatedAt(ZonedDateTime.now());
        roomRepository.save(room3);
    }

}
