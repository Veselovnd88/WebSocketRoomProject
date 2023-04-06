package ru.veselov.websocketroomproject.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
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
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatMessageControllerTest {

    private static String ROOM_ID = "5";
    @LocalServerPort
    private String port;
    BlockingQueue<SendChatMessage> blockingQueue;

    @Value("${socket.chat-topic}")
    private String chatDestination;

    @Value("${socket.endpoint}")
    private String endpoint;

    @Autowired
    WebTestClient webTestClient;

    private String URL;
    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;

    @MockBean
    EventMessageService eventMessageService;
    @MockBean
    RoomSubscriptionService roomSubscriptionService;

    @BeforeEach
    void setUp() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        SubscriptionData subscriptionData = Mockito.mock(SubscriptionData.class);
        URL = "ws://localhost:" + port + endpoint;
        blockingQueue = new LinkedBlockingDeque<>();
        Mockito.when(roomSubscriptionService.findSubscription(ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString())).thenReturn(subscriptionData);
        Mockito.when(subscriptionData.getFluxSink()).thenReturn(fluxSink);
    }


    @Test
    void shouldSendMessageAndSetAuthenticationUsername() throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = "user1" + ":" + "secret";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("roomId", ROOM_ID);
        WebSocketStompClient stompClient = new WebSocketStompClient(
                new SockJsClient(createTransportClient())
        );
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = stompClient.connectAsync(URL, headers, stompHeaders,
                new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);
        session.subscribe(chatDestination, new DefaultStompFrameHandler());

        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("Vasya", "message");

        session.send("/app/chat/" + ROOM_ID, receivedChatMessage);


    }


    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }


    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return SendChatMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer((SendChatMessage) o);
        }
    }


}

