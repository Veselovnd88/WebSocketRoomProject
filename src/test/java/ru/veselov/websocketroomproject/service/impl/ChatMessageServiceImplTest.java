package ru.veselov.websocketroomproject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.websocketroomproject.dto.request.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.response.SendChatMessage;
import ru.veselov.websocketroomproject.mapper.ChatMessageMapper;
import ru.veselov.websocketroomproject.mapper.ChatMessageMapperImpl;

import java.security.Principal;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    private static final String ROOM_ID = "5";

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    ChatMessageServiceImpl chatMessageService;

    @BeforeEach
    void init() {
        ChatMessageMapper chatMessageMapper = new ChatMessageMapperImpl();
        ReflectionTestUtils.setField(chatMessageMapper, "serverZoneId", "Europe/Moscow", String.class);
        ReflectionTestUtils.setField(
                chatMessageService,
                "chatMessageMapper",
                chatMessageMapper,
                ChatMessageMapper.class);
    }

    @Test
    void shouldSendMessageToTopic() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("user1");
        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("user1", "msg", null);

        chatMessageService.sendToTopic(ROOM_ID, receivedChatMessage, principal);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(SendChatMessage.class));
    }

    @Test
    void shouldSendMessageToUser() {
        ReceivedChatMessage receivedChatMessage = new ReceivedChatMessage("user1", "msg", "session1");

        chatMessageService.sendToUser(receivedChatMessage, "session1");

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(SendChatMessage.class));
    }

}