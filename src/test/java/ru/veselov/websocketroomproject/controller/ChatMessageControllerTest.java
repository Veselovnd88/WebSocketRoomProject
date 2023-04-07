package ru.veselov.websocketroomproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.controller.client.TestStompFrameHandler;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.listener.WebSocketDisconnectListener;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

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
    ChatMessageController chatMessageController;

    @MockBean
    WebSocketDisconnectListener webSocketDisconnectListener;
    @MockBean
    RoomSubscriptionService roomSubscriptionService;

    @BeforeEach
    void setUp() {
        SubscriptionData subscriptionData = Mockito.mock(SubscriptionData.class);
        URL = "ws://localhost:" + port + endpoint;
    }


    @Test
    void shouldSendMessageAndSetAuthenticationUsername() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<SendChatMessage> resultKeeper = new CompletableFuture<>();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = "user1" + ":" + "secret";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("roomId", ROOM_ID);
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = stompClient.connectAsync(URL, headers, stompHeaders,
                new StompSessionHandlerAdapter() {
                }).get(100, TimeUnit.SECONDS);
        String destination = "/topic/messages" + "/" + ROOM_ID;
        session.subscribe(destination, new TestStompFrameHandler(resultKeeper::complete));
        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("Vasya", "message");
        session.send("/app/chat/" + ROOM_ID, receivedChatMessage);
        Thread.sleep(1000);
        SendChatMessage sendChatMessage = resultKeeper.get(1, TimeUnit.SECONDS);
        Assertions.assertThat(sendChatMessage).isNotNull();


    }


}

