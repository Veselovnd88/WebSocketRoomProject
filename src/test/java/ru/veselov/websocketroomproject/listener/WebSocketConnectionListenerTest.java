package ru.veselov.websocketroomproject.listener;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.handler.UserConnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.jwt.impl.JwtHelperImpl;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.websocket.listener.WebSocketConnectionListener;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class WebSocketConnectionListenerTest {

    private static final String DESTINATION = "/topic/users/5";

    @Mock
    ChatUserService chatUserService;

    @Mock
    UserConnectEventHandler userConnectEventHandler;

    @Mock
    RoomService roomService;

    WebSocketConnectionListener webSocketConnectionListener;

    @Captor
    ArgumentCaptor<ChatUser> chatUserArgumentCaptor;

    @BeforeEach
    void init() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setHeader("Authorization");
        authProperties.setPrefix("Bearer ");
        authProperties.setSecret(TestConstants.SECRET);
        webSocketConnectionListener = new WebSocketConnectionListener(
                userConnectEventHandler,
                chatUserService,
                new JwtHelperImpl(authProperties),
                authProperties,
                roomService);
        ReflectionTestUtils.setField(
                webSocketConnectionListener,
                "roomIdHeader",
                TestConstants.ROOM_ID_HEADER,
                String.class);
    }

    @Test
    void shouldSaveUserAndNotifyUsers() {
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                StompHeaderAccessor.DESTINATION_HEADER, DESTINATION,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        TestConstants.ROOM_ID_HEADER, List.of(TestConstants.ROOM_ID),
                        TestConstants.AUTH_HEADER, List.of(TestConstants.BEARER_JWT)
                )
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionConnectEvent sessionSubscribeEvent = new SessionConnectEvent(new Object(), message);

        webSocketConnectionListener.handleUserConnection(sessionSubscribeEvent);

        Mockito.verify(chatUserService, Mockito.times(1))
                .saveChatUser(chatUserArgumentCaptor.capture());
        ChatUser captured = chatUserArgumentCaptor.getValue();
        Assertions.assertThat(captured).isNotNull().isInstanceOf(ChatUser.class);
        Assertions.assertThat(captured.getSession()).isEqualTo(TestConstants.TEST_SESSION_ID);
        Assertions.assertThat(captured.getUsername()).isNotBlank();
        Assertions.assertThat(captured.getRoomId()).isEqualTo(TestConstants.ROOM_ID);
        Mockito.verify(userConnectEventHandler, Mockito.times(1)).handleConnectEvent(captured);
        Mockito.verify(roomService, Mockito.times(1)).addUserCount(TestConstants.ROOM_ID, captured.getUsername());
    }

}