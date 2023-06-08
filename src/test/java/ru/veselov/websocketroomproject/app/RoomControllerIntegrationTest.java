package ru.veselov.websocketroomproject.app;

import net.datafaker.Faker;
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
import ru.veselov.websocketroomproject.model.Room;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class RoomControllerIntegrationTest extends PostgresContainersConfig {

    Faker faker = new Faker();

    private final static String ROOM_ID = "ec1edd63-4080-480b-84cc-2faee587999f";

    @Autowired
    WebTestClient webTestClient;


    @Test
    void shouldCreateAndReturnRoom() {
        Room transferedRoom = Room.builder()
                .name(faker.elderScrolls().region())
                .ownerName(faker.elderScrolls().lastName())
                .isPrivate(true).build();
        WebTestClient.BodyContentSpec resultBody = webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("/api").path("/room").path("/create").build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .bodyValue(transferedRoom)
                .exchange().expectStatus().isCreated().expectBody();
    }

    private void validateReturnedRoomBody(WebTestClient.BodyContentSpec resultBody, Room room) {
        resultBody
                .jsonPath("$.ownerName").isEqualTo(room.getOwnerName())
                .jsonPath("$.name").isEqualTo(room.getName())
                .jsonPath("$.id").isEqualTo(room.getId().toString())
                .jsonPath("$.isPrivate").isEqualTo(room.getIsPrivate())
                .jsonPath("$.activeUrl").isEqualTo(room.getActiveUrl())
                .jsonPath("$.roomToken").isEqualTo(room.getRoomToken())
                .jsonPath("$.playerType").isEqualTo(room.getPlayerType().toString())
                .jsonPath("$.createdAt").isEqualTo(room.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")))
                .jsonPath("$.changedAt").isEqualTo(room.getChangedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")));
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
