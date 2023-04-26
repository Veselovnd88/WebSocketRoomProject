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
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;
import ru.veselov.websocketroomproject.listener.WebSocketDisconnectListener;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatMessageControllerTest {

    private static final String ROOM_ID = "5";

    private static final String AUTH_HEADER = "Authorization";

    private static final String BEARER_JWT = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidX" +
            "Nlcm5hbWUiOiJ1c2VyMSIsInJvbGUiOiJhZG1pbiJ9.vDluIRzAjSOxbq8I4tLPUR_koUl7GPkAq34xjsuA1Ds";

    @Value("${server.zoneId}")
    private String serverZoneId;

    @LocalServerPort
    private String port;

    @Value("${socket.chat-topic}")
    private String chatTopic;

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
    WebSocketDisconnectListener webSocketDisconnectListener;

    @MockBean
    ChatUserService chatUserService;

    CompletableFuture<SendChatMessage> resultKeeper;

    @BeforeEach
    void setUp() {
        URL = "ws://localhost:" + port + endpoint;
        resultKeeper = new CompletableFuture<>();
    }

    @Test
    @SneakyThrows
    void shouldReturnCorrectSendMessage() {
        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("user1", "message", null);
        String destination = chatTopic + "/" + ROOM_ID;
        StompHeaders stompHeadersConnect = new StompHeaders();
        stompHeadersConnect.add(TestConstants.ROOM_ID_HEADER, ROOM_ID);
        stompHeadersConnect.add(AUTH_HEADER, BEARER_JWT);
        //Creating and configuring basic WebSocketClient
        WebSocketStompClient stompClient = createClient();
        StompSession session = stompClient.connectAsync(URL,
                new WebSocketHttpHeaders(),
                stompHeadersConnect,
                new TestStompSessionHandlerAdapter()
        ).get();

        session.subscribe(destination,
                new TestStompFrameHandler<>(resultKeeper::complete, SendChatMessage.class));
        StompHeaders stompHeadersSend = new StompHeaders();
        stompHeadersSend.add(AUTH_HEADER, BEARER_JWT);
        stompHeadersSend.add(StompHeaders.DESTINATION, "/app/chat/" + ROOM_ID);
        session.send(stompHeadersSend, receivedChatMessage);

        SendChatMessage sendChatMessage = resultKeeper.get(3, TimeUnit.SECONDS);
        Assertions.assertThat(sendChatMessage).isNotNull();
        Assertions.assertThat(sendChatMessage.getSentFrom()).isEqualTo("user1");
        Assertions.assertThat(sendChatMessage.getContent()).isEqualTo("message");
        Assertions.assertThat(sendChatMessage.getSentTime()).isNotNull().isInstanceOf(ZonedDateTime.class);
        //creating new zdt instance to check is returned time a little less than now
        ZonedDateTime zdtToCompare = ZonedDateTime.now(ZoneId.of(serverZoneId)).plusSeconds(1);
        long diff = zdtToCompare.toEpochSecond() - sendChatMessage.getSentTime().toEpochSecond();
        Assertions.assertThat(diff).isLessThan(3);
    }

    @Test
    @SneakyThrows
    void shouldReturnCorrectSendMessageToUser() {
        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("user1", "message", "user1");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = "user1" + ":" + "secret";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(TestConstants.ROOM_ID_HEADER, ROOM_ID);
        //Creating and configuring basic WebSocketClient
        WebSocketStompClient stompClient = createClient();
        StompSession session = stompClient.connectAsync(URL, headers, stompHeaders, new TestStompSessionHandlerAdapter()
        ).get();
        session.subscribe("/user/queue/private",
                new TestStompFrameHandler<>(resultKeeper::complete, SendChatMessage.class));
        session.send("/app/chat-private", receivedChatMessage);
        SendChatMessage sendChatMessage = resultKeeper.get(3, TimeUnit.SECONDS);
        Assertions.assertThat(sendChatMessage).isNotNull();
        Assertions.assertThat(sendChatMessage.getSentFrom()).isEqualTo("user1");
        Assertions.assertThat(sendChatMessage.getContent()).isEqualTo("message");
        Assertions.assertThat(sendChatMessage.getSentTime()).isNotNull().isInstanceOf(ZonedDateTime.class);
        //creating new zdt instance to check is returned time a little less than now
        ZonedDateTime zdtToCompare = ZonedDateTime.now(ZoneId.of(serverZoneId)).plusSeconds(1);
        long diff = zdtToCompare.toEpochSecond() - sendChatMessage.getSentTime().toEpochSecond();
        Assertions.assertThat(diff).isLessThan(3);
    }

    @Test
    @SneakyThrows
    void shouldNotReturnSendMessageToUser() {
        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("user1", "message", "not-existing-user");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = "user1" + ":" + "secret";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(TestConstants.ROOM_ID_HEADER, ROOM_ID);
        //Creating and configuring basic WebSocketClient
        WebSocketStompClient stompClient = createClient();
        StompSession session = stompClient.connectAsync(URL, headers, stompHeaders, new TestStompSessionHandlerAdapter()
        ).get();

        session.subscribe("/user/queue/private",
                new TestStompFrameHandler<>(resultKeeper::complete, SendChatMessage.class));
        session.send("/app/chat-private", receivedChatMessage);

        //Will be thrown TimeoutException if no message received by User
        Assertions.assertThatThrownBy(() -> resultKeeper.get(3, TimeUnit.SECONDS)).isInstanceOf(TimeoutException.class);
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