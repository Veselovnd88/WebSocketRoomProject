package ru.veselov.websocketroomproject.listener;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.List;
import java.util.Map;

@SpringBootTest
@WithMockUser(username = "user1")
class WebSocketConnectionListenerTest {

    @Autowired
    private WebSocketConnectionListener webSocketConnectionListener;
    @MockBean
    ChatUserService chatUserService;
    @Captor
    ArgumentCaptor<ChatUser> chatUserArgumentCaptor;

    @Test
    void shouldSaveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "simpDestination", "/topic/users/5",
                "simpSessionId", "test",
                "nativeHeaders", Map.of("roomId", List.of("5")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionConnectEvent sessionSubscribeEvent = new SessionConnectEvent(new Object(), message, authentication);

        webSocketConnectionListener.handleUserConnection(sessionSubscribeEvent);

        Mockito.verify(chatUserService, Mockito.times(1))
                .saveChatUser(chatUserArgumentCaptor.capture());
        ChatUser chatUserFromCaptor = chatUserArgumentCaptor.getValue();
        Assertions.assertThat(chatUserFromCaptor).isNotNull().isInstanceOf(ChatUser.class);
        Assertions.assertThat(chatUserFromCaptor.getSession()).isEqualTo("test");
        Assertions.assertThat(chatUserFromCaptor.getUsername()).isEqualTo("user1");
        Assertions.assertThat(chatUserFromCaptor.getRoomId()).isEqualTo("5");
    }

}