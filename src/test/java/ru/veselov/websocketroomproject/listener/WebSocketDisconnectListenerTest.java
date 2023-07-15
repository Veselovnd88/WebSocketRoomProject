package ru.veselov.websocketroomproject.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.handler.UserDisconnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.websocket.listener.WebSocketDisconnectListener;

import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked"})
class WebSocketDisconnectListenerTest {

    @Mock
    private ChatUserService chatUserService;

    @Mock
    private UserDisconnectEventHandler userDisconnectEventHandler;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private WebSocketDisconnectListener webSocketDisconnectListener;

    @Captor
    ArgumentCaptor<ChatUser> chatUserCaptor;

    @Test
    void shouldRemoveUserFromCacheAndCompleteSubscriptionAndSendMessage() {
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID);
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionDisconnectEvent sessionDisconnectEvent = new SessionDisconnectEvent(new Object(),
                message,
                TestConstants.TEST_SESSION_ID,
                CloseStatus.NORMAL);
        Mockito.when(chatUserService.removeChatUser(TestConstants.TEST_SESSION_ID)).thenReturn(
                Optional.of(new ChatUser(
                        TestConstants.TEST_USERNAME,
                        TestConstants.ROOM_ID,
                        TestConstants.TEST_SESSION_ID)
                )
        );

        webSocketDisconnectListener.handleUserDisconnect(sessionDisconnectEvent);

        Mockito.verify(chatUserService, Mockito.times(1)).removeChatUser(TestConstants.TEST_SESSION_ID);
        Mockito.verify(userDisconnectEventHandler, Mockito.times(1)).handleDisconnectEvent(chatUserCaptor.capture());
        ChatUser captured = chatUserCaptor.getValue();
        Mockito.verify(roomService, Mockito.times(1)).decreaseUserCount(TestConstants.ROOM_ID, captured.getUsername());
    }

    @Test
    void shouldDoNothingAndLogActionIfNoChatUserFound() {
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID);
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionDisconnectEvent sessionDisconnectEvent = new SessionDisconnectEvent(new Object(),
                message,
                TestConstants.TEST_SESSION_ID,
                CloseStatus.NORMAL);
        Mockito.when(chatUserService.removeChatUser(TestConstants.TEST_USERNAME)).thenReturn(Optional.empty());

        webSocketDisconnectListener.handleUserDisconnect(sessionDisconnectEvent);

        Mockito.verify(chatUserService, Mockito.times(1)).removeChatUser(TestConstants.TEST_SESSION_ID);
        Mockito.verify(userDisconnectEventHandler, Mockito.never()).handleDisconnectEvent(chatUserCaptor.capture());
        Mockito.verify(roomService, Mockito.never()).decreaseUserCount(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());
    }

}
