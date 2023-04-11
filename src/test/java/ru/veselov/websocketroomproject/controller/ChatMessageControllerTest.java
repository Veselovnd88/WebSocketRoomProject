package ru.veselov.websocketroomproject.controller;

import lombok.extern.slf4j.Slf4j;
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
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatMessageControllerTest {

    private static String ROOM_ID = "5";
    @LocalServerPort
    private String port;

    @Value("${socket.chat-topic}")
    private String chatDestination;

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
    WebSocketDisconnectListener webSocketDisconnectListener;
    @MockBean
    RoomSubscriptionService roomSubscriptionService;

    @BeforeEach
    void setUp() {
        URL = "ws://localhost:" + port + endpoint;
    }

    @Test
    void shouldReturnCorrectSendMessage() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<SendChatMessage> resultKeeper = new CompletableFuture<>();
        ReceivedChatMessage receivedChatMessage =
                new ReceivedChatMessage(TestConstants.TEST_USERNAME, "message", null, ROOM_ID);
        String destination = chatDestination + "/" + ROOM_ID;
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = "user1" + ":" + "secret";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(TestConstants.ROOM_ID_HEADER, ROOM_ID);
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

        StompSession session = stompClient.connectAsync(URL, headers, stompHeaders,
                        new TestStompSessionHandlerAdapter())
                .get();
        session.subscribe(destination, new TestStompFrameHandler(resultKeeper::complete));
        session.send("/app/chat/" + ROOM_ID, receivedChatMessage);

        Thread.sleep(1000); //giving time to server for response
        SendChatMessage sendChatMessage = resultKeeper.get(1, TimeUnit.SECONDS);
        Assertions.assertThat(sendChatMessage).isNotNull();
        Assertions.assertThat(sendChatMessage.getSentFrom()).isEqualTo(TestConstants.TEST_USERNAME);
        Assertions.assertThat(sendChatMessage.getContent()).isEqualTo("message");
        Assertions.assertThat(sendChatMessage.getSentTime()).isNotNull().isInstanceOf(ZonedDateTime.class);
    }

}