package ru.veselov.websocketroomproject.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;
import ru.veselov.websocketroomproject.service.ChatMessageService;

import java.security.Principal;

@SpringBootTest
@WithMockUser(username = "user1")
class ChatMessageServiceImplTest {

    private static final String ROOM_ID = "5";

    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    ChatMessageService chatMessageService;

    @Test
    void shouldSendMessageToTopic() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("user1");
        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("user1", "msg", null, "Europe/Moscow");

        chatMessageService.sendToTopic(ROOM_ID, receivedChatMessage, principal);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(SendChatMessage.class));
    }

    @Test
    void shouldSendMessageToUser() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("user1");
        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("user1", "msg", "user1", "Europe/Moscow");

        chatMessageService.sendToUser(receivedChatMessage, principal);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(SendChatMessage.class)
                );
    }

}