package ru.veselov.websocketroomproject.listener;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
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
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.MessageType;
import ru.veselov.websocketroomproject.dto.SendMessageDTO;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
@WithMockUser(username = "testUser")
class WebSocketSubscriptionListenerTest {
    @Autowired
    private WebSocketSubscriptionListener webSocketSubscriptionListener;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    @MockBean
    private ChatUserService chatUserService;

    @Captor
    ArgumentCaptor<SendMessageDTO<List<ChatUserDTO>>> argumentCaptor;

    @Test
    void shouldConnectToChosenTopic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "simpDestination", "/topic/users/5",
                "simpSessionId", "test",
                "nativeHeaders", Map.of("roomId", List.of("5")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);
        Mockito.when(chatUserService.findChatUsersByRoomId("5")).thenReturn(Set.of(
                new ChatUser(
                        "vasya",
                        "5",
                        "session1"
                ),
                new ChatUser(
                        "petya",
                        "5",
                        "session2"
                )
        ));
        webSocketSubscriptionListener.handleUserSubscription(sessionSubscribeEvent);
        Mockito.verify(chatUserService, Mockito.times(1)).findChatUsersByRoomId("5");
        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), argumentCaptor.capture());
        Assertions.assertThat(argumentCaptor.getValue()).isInstanceOf(SendMessageDTO.class).isNotNull();
        Assertions.assertThat(argumentCaptor.getValue().getMessageType())
                .isEqualTo(MessageType.USERS);
        Assertions.assertThat(argumentCaptor.getValue().getMessage()).hasSize(2).hasAtLeastOneElementOfType(ChatUserDTO.class);

    }

    @Test
    void shouldNotConnectToNotChosenTopic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "simpDestination", "/topic/notUsers/5",
                "simpSessionId", "test",
                "nativeHeaders", Map.of("roomId", List.of("5")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketSubscriptionListener.handleUserSubscription(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class));
    }

}