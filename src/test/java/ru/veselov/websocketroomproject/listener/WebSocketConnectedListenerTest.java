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
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;

import java.util.Map;

@SpringBootTest
class WebSocketConnectedListenerTest {

    private static final String ROOM_ID = "5";
    @Autowired
    private WebSocketConnectedListener webSocketConnectedListener;

    @MockBean
    ChatUserService chatUserService;
    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;
    @MockBean
    EventMessageService eventMessageService;
    @Captor
    ArgumentCaptor<ChatUser> chatUserCaptor;

    @Test
    void shouldSendMessageAndUserList() {
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID);
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionConnectedEvent sessionConnectedEvent = new SessionConnectedEvent(new Object(), message);
        Mockito.when(chatUserService.findChatUserBySessionId(TestConstants.TEST_SESSION_ID)).thenReturn(new ChatUser(
                TestConstants.TEST_USERNAME,
                ROOM_ID,
                TestConstants.TEST_SESSION_ID)
        );

        webSocketConnectedListener.handleConnectedUserEvent(sessionConnectedEvent);

        Mockito.verify(chatUserService, Mockito.times(1)).findChatUserBySessionId(TestConstants.TEST_SESSION_ID);
        Mockito.verify(eventMessageService, Mockito.times(1)).sendUserConnectedMessageToAll(chatUserCaptor.capture());
        Mockito.verify(eventMessageService, Mockito.times(1))
                .sendUserListToAllSubscriptions(ArgumentMatchers.anyString());
        ChatUser captorValue = chatUserCaptor.getValue();
        Assertions.assertThat(captorValue.getUsername()).isEqualTo(TestConstants.TEST_USERNAME);
    }

}