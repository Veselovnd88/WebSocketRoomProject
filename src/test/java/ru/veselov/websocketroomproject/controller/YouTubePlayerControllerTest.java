package ru.veselov.websocketroomproject.controller;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.controller.client.TestStompFrameHandler;
import ru.veselov.websocketroomproject.controller.client.TestStompSessionHandlerAdapter;
import ru.veselov.websocketroomproject.dto.PlayerStateDTO;
import ru.veselov.websocketroomproject.event.UserDisconnectEventHandler;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class YouTubePlayerControllerTest {

    private static final String ROOM_ID = "5";

    @LocalServerPort
    private String port;

    @Value("${socket.youtube-topic}")
    private String youtubeTopic;

    @Value("${socket.endpoint}")
    private String endpoint;

    private String URL;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    MappingJackson2MessageConverter jackson2MessageConverter;

    @Autowired
    ChatMessageController chatMessageController;

    @MockBean
    RoomSubscriptionService roomSubscriptionService;

    @MockBean
    UserDisconnectEventHandler userDisconnectEventHandler;

    @MockBean
    ChatUserService chatUserService;

    CompletableFuture<PlayerStateDTO> playerStateResultKeeper;

    @BeforeEach
    void setUp() {
        URL = "ws://localhost:" + port + endpoint;
        playerStateResultKeeper = new CompletableFuture<>();
    }

    @Test
    @SneakyThrows
    void shouldReturnSentPlayerStateDTO() {
        PlayerStateDTO playerStateDTO = new PlayerStateDTO(1, "111.111", "low", "1");
        String destination = youtubeTopic + "/" + ROOM_ID;
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = "user1" + ":" + "secret";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(TestConstants.ROOM_ID_HEADER, ROOM_ID);
        //Creating and configuring basic WebSocketClient
        WebSocketStompClient stompClient = createClient();
        StompSession session = stompClient.connectAsync(URL, headers, stompHeaders, new TestStompSessionHandlerAdapter()
        ).get();

        session.subscribe(destination,
                new TestStompFrameHandler<>(playerStateResultKeeper::complete, PlayerStateDTO.class));
        session.send("/app/youtube/" + ROOM_ID, playerStateDTO);

        PlayerStateDTO receivedPlayerState = playerStateResultKeeper.get(3, TimeUnit.SECONDS);
        Assertions.assertThat(receivedPlayerState).isNotNull().isEqualTo(playerStateDTO);
    }

    private WebSocketStompClient createClient() {
        //Creating and configuring basic WebSocketClient
        WebSocketStompClient stompClient = new WebSocketStompClient(
                new SockJsClient(
                        Collections.singletonList(
                                new WebSocketTransport(
                                        new StandardWebSocketClient())
                        )
                )
        );
        stompClient.setMessageConverter(jackson2MessageConverter);
        return stompClient;
    }

}