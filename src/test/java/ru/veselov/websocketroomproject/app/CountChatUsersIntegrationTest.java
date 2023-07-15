package ru.veselov.websocketroomproject.app;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.app.containers.PostgresAndRedisContainersConfig;
import ru.veselov.websocketroomproject.controller.client.TestStompFrameHandler;
import ru.veselov.websocketroomproject.controller.client.TestStompSessionHandlerAdapter;
import ru.veselov.websocketroomproject.dto.response.SendChatMessage;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class CountChatUsersIntegrationTest extends PostgresAndRedisContainersConfig {

    public static final String URL_PREFIX = "/api/v1/room/";

    public static final String MOVIE = "Movie";

    public String URL;

    @LocalServerPort
    private String port;

    @Value("${socket.chat-topic}")
    private String chatTopic;

    @Value("${socket.endpoint}")
    private String endpoint;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    RoomService roomService;

    CompletableFuture<SendChatMessage> resultKeeper;


    @BeforeEach
    void init() {
        tagRepository.deleteAll();
        UriComponents url = UriComponentsBuilder.newInstance()
                .scheme("ws").host("localhost").port(port).path(endpoint).build();
        URL = url.toUriString();
        resultKeeper = new CompletableFuture<>();
    }

    @AfterEach
    void clearAll() {
        roomRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldAddConnectedAndRemoveDisconnectedUserToRoom() {
        fillRepoWithRooms();
        Room roomA = roomService.getRoomByName("aaa");
        String roomId = roomA.getId().toString();
        String destination = chatTopic + "/" + roomId;
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(TestConstants.ROOM_ID_HEADER, roomId);
        stompHeaders.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT);
        WebSocketStompClient stompClient = createClient();
        StompSession session = stompClient.connectAsync(URL,
                new WebSocketHttpHeaders(),
                stompHeaders,
                new TestStompSessionHandlerAdapter()
        ).get();//connecting to websocket endpoint

        //when
        session.subscribe(destination,
                new TestStompFrameHandler<>(resultKeeper::complete, SendChatMessage.class));

        Awaitility.await().pollDelay(Duration.ofMillis(2000)).until(() -> true);
        Optional<RoomEntity> increasedUsersRoom = roomRepository.findByName("aaa");
        Assertions.assertThat(increasedUsersRoom).isPresent();
        Assertions.assertThat(increasedUsersRoom.get().getMaxUserQnt()).isEqualTo(1);
        Assertions.assertThat(increasedUsersRoom.get().getUserQnt()).isEqualTo(1);

        session.disconnect();
        Awaitility.await().pollDelay(Duration.ofMillis(2000)).until(() -> true);
        Optional<RoomEntity> decreasedUsersRoom = roomRepository.findByName("aaa");
        Assertions.assertThat(decreasedUsersRoom).isPresent();
        Assertions.assertThat(decreasedUsersRoom.get().getMaxUserQnt()).isEqualTo(1);
        Assertions.assertThat(decreasedUsersRoom.get().getUserQnt()).isZero();
    }

    public void fillRepoWithRooms() {
        roomRepository.deleteAll();//clear
        tagRepository.deleteAll();
        tagRepository.save(new TagEntity(MOVIE));
        Principal principal1 = Mockito.mock(Principal.class);
        Mockito.when(principal1.getName()).thenReturn("xxx");
        Room room1 = Room.builder().playerType(PlayerType.YOUTUBE).name("aaa").isPrivate(false).ownerName("xxx")
                .tags(Set.of(new Tag(MOVIE)))
                .build();
        roomService.createRoom(room1, principal1);

    }

    private WebSocketStompClient createClient() {
        //Creating and configuring basic WebSocketClient
        return new WebSocketStompClient(
                new SockJsClient(
                        Collections.singletonList(
                                new WebSocketTransport(
                                        new StandardWebSocketClient())
                        )
                )
        );
    }

}
