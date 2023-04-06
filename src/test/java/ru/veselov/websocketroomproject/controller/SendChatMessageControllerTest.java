package ru.veselov.websocketroomproject.controller;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.veselov.websocketroomproject.config.interceptor.SocketConnectionInterceptor;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("user1")
//@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
class ChatMessageControllerTest {

    private static String ROOM_ID = "5";
    @LocalServerPort
    private String port;
    BlockingQueue<SendChatMessage> blockingQueue;

    @Value("${socket.chat-topic}")
    private String chatDestination;

    @Value("${socket.endpoint}")
    private String endpoint;


    private String URL;
    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;

    @MockBean
    SimpUserRegistry simpUserRegistry;


    @MockBean
    Authentication authentication;


    @BeforeEach
    void setUp() {
        URL = "ws://localhost:" + port + endpoint;
        blockingQueue = new LinkedBlockingDeque<>();
    }


    @Test
    void shouldSendMessageAndSetAuthenticationUsername() throws ExecutionException, InterruptedException, TimeoutException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Principal principal = Mockito.mock(Principal.class);
        simpUserRegistry.r
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        Mockito.when(principal.getName()).thenReturn("user1");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("user1","secret");
        Mockito.when(authentication.getName()).thenReturn("user1");
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("roomId", ROOM_ID);
        WebSocketStompClient stompClient = new WebSocketStompClient(
                new SockJsClient(createTransportClient())
        );
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = stompClient.connectAsync(URL,headers,stompHeaders, new StompSessionHandlerAdapter() {}).get(1,TimeUnit.SECONDS);
        session.subscribe(chatDestination, new DefaultStompFrameHandler());

        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("Vasya", "message");

        session.send("/app/chat" + ROOM_ID, receivedChatMessage);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(SendChatMessage.class));

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

