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
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.MessageType;
import ru.veselov.websocketroomproject.dto.SendMessageDTO;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.Map;

@SpringBootTest
class WebSocketConnectedListenerTest {
    @Autowired
    private WebSocketConnectedListener webSocketConnectedListener;

    @MockBean
    ChatUserService chatUserService;
    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;
    @Captor
    ArgumentCaptor<SendMessageDTO<ChatUserDTO>> messageDTOArgumentCaptor;

    @Test
    void shouldSendMessage() {
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "simpSessionId", "test");
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionConnectedEvent sessionConnectedEvent = new SessionConnectedEvent(new Object(), message);
        Mockito.when(chatUserService.findChatUserBySessionId("test")).thenReturn(new ChatUser(
                "testName",
                "5",
                "test"
        ));

        webSocketConnectedListener.handleConnectedUserEvent(sessionConnectedEvent);

        Mockito.verify(chatUserService, Mockito.times(1))
                .findChatUserBySessionId("test");
        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), messageDTOArgumentCaptor.capture());
        SendMessageDTO<ChatUserDTO> messageDTOArgumentCaptorValue = messageDTOArgumentCaptor.getValue();
        Assertions.assertThat(messageDTOArgumentCaptorValue.getMessageType()).isEqualTo(MessageType.CONNECTED);
        Assertions.assertThat(messageDTOArgumentCaptorValue.getMessage().getUsername()).isEqualTo("testName");
    }

}