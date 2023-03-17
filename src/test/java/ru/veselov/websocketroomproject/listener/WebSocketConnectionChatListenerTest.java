package ru.veselov.websocketroomproject.listener;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@WithMockUser(username = "testUser")
class WebSocketConnectionChatListenerTest {
    @Autowired
    private WebSocketConnectionChatListener webSocketConnectionChatListener;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void shouldConnectToChosenTopic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("simpDestination", "/topic/users/5");
        headers.put("simpSessionId", "test");
        headers.put("nativeHeaders", Map.of("roomId", List.of("5")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketConnectionChatListener.handleUserSubscription(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class));
    }

    @Test
    void shouldNotConnectToNotChosenTopic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("simpDestination", "/topic/notUsers/5");
        headers.put("simpSessionId", "test");
        headers.put("nativeHeaders", Map.of("roomId", List.of("5")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketConnectionChatListener.handleUserSubscription(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class));
    }
}