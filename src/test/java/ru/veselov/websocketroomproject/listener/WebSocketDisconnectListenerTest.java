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
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.SendMessageDTO;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.Map;

@SpringBootTest
class WebSocketDisconnectListenerTest {

    @MockBean
    private ChatUserService chatUserService;

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private WebSocketDisconnectListener webSocketDisconnectListener;
    @Captor
    ArgumentCaptor<SendMessageDTO<ChatUserDTO>> messageDTOArgumentCaptor;

    @Test
    void shouldSendMessage() {
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "simpSessionId", "test");
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionDisconnectEvent sessionDisconnectEvent = new SessionDisconnectEvent(new Object(),
                message,
                "sessionId",
                CloseStatus.NORMAL);
        Mockito.when(chatUserService.removeChatUser("test")).thenReturn(new ChatUser(
                "testName",
                "5",
                "test")
        );

        webSocketDisconnectListener.handleUserDisconnect(sessionDisconnectEvent);

        Mockito.verify(chatUserService, Mockito.times(1))
                .removeChatUser("test");
        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), messageDTOArgumentCaptor.capture());
        SendMessageDTO<ChatUserDTO> messageDTOArgumentCaptorValue = messageDTOArgumentCaptor.getValue();
        Assertions.assertThat(messageDTOArgumentCaptorValue.getMessage().getUsername()).isEqualTo("testName");
    }

}