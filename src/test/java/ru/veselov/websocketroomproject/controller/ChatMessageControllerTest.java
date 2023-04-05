package ru.veselov.websocketroomproject.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.veselov.websocketroomproject.dto.ChatMessage;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("user1")
class ChatMessageControllerTest {

    @Value("${local.server.port}")
    private int port;
    @Value("${socket.chat-topic}")
    private String chatDestination;

    private String URL;

    @Autowired
    ChatMessageController controller;
    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {

    }


    @Test
    void shouldSendMessageAndSetAuthenticationUsername() {
        WebSocketStompClient stompClient = new WebSocketStompClient(
                new SockJsClient(createTransportClient())
        );
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());


        ChatMessage chatMessage = new ChatMessage(null, "message", null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        controller.processMessage("5", chatMessage, authentication);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatMessage.class));
        Assertions.assertThat(chatMessage.getSentFrom()).isEqualTo("user1");
        Assertions.assertThat(chatMessage.getSent()).isNotNull();
    }

    @Test
    void shouldSendMessageWithExistingUsername() {
        ChatMessage chatMessage = new ChatMessage("evil", "message", null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        controller.processMessage("5", chatMessage, authentication);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatMessage.class));
        Assertions.assertThat(chatMessage.getSentFrom()).isEqualTo("evil");
        Assertions.assertThat(chatMessage.getSent()).isNotNull();
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

}