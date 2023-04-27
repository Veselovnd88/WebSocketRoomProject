package ru.veselov.websocketroomproject.listener;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.UserConnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.List;
import java.util.Map;

@SpringBootTest
@SuppressWarnings("unchecked")
class WebSocketConnectionListenerTest {

    private static final String ROOM_ID = "5";

    private static final String DESTINATION = "/topic/users/5";

    private static final String AUTH_HEADER = "Authorization";

    private static final String BEARER_JWT = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidX" +
            "Nlcm5hbWUiOiJ1c2VyMSIsInJvbGUiOiJhZG1pbiJ9.vDluIRzAjSOxbq8I4tLPUR_koUl7GPkAq34xjsuA1Ds";

    @Value("${socket.header-room-id}")
    private String roomIdHeader;

    @Autowired
    private WebSocketConnectionListener webSocketConnectionListener;
    @MockBean
    ChatUserService chatUserService;

    @MockBean
    UserConnectEventHandler userConnectEventHandler;

    @Captor
    ArgumentCaptor<ChatUser> chatUserArgumentCaptor;

    @Test
    void shouldSaveUserAndNotifyUsers() {
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                StompHeaderAccessor.DESTINATION_HEADER, DESTINATION,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        roomIdHeader, List.of(ROOM_ID),
                        AUTH_HEADER, List.of(BEARER_JWT)
                )
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionConnectEvent sessionSubscribeEvent = new SessionConnectEvent(new Object(), message);

        webSocketConnectionListener.handleUserConnection(sessionSubscribeEvent);

        Mockito.verify(chatUserService, Mockito.times(1))
                .saveChatUser(chatUserArgumentCaptor.capture());
        ChatUser chatUserFromCaptor = chatUserArgumentCaptor.getValue();
        Assertions.assertThat(chatUserFromCaptor).isNotNull().isInstanceOf(ChatUser.class);
        Assertions.assertThat(chatUserFromCaptor.getSession()).isEqualTo(TestConstants.TEST_SESSION_ID);
        Assertions.assertThat(chatUserFromCaptor.getUsername()).isNotBlank();
        Assertions.assertThat(chatUserFromCaptor.getRoomId()).isEqualTo(ROOM_ID);
        Mockito.verify(userConnectEventHandler, Mockito.times(1)).handleConnectEvent(chatUserFromCaptor);
    }

}