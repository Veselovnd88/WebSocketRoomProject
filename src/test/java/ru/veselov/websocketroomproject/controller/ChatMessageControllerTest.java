package ru.veselov.websocketroomproject.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.veselov.websocketroomproject.dto.ChatMessage;

@SpringBootTest
@WithMockUser("user1")
class ChatMessageControllerTest {

    @Value("${socket.chat-topic}")
    private String chatDestination;

    @Autowired
    ChatMessageController controller;
    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void shouldSendMessageAndSetAuthenticationUsername() {
        ChatMessage chatMessage = new ChatMessage(null, "message", null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        controller.processMessage("5", chatMessage, authentication);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatMessage.class));
        Assertions.assertThat(chatMessage.getSentFrom()).isEqualTo("user1");
        Assertions.assertThat(chatMessage.getSent()).isNotNull();
    }

    @Test
    void shouldSendMessageWithExistingUsername() {
        ChatMessage chatMessage = new ChatMessage("evil", "message", null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        controller.processMessage("5", chatMessage, authentication);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatMessage.class));
        Assertions.assertThat(chatMessage.getSentFrom()).isEqualTo("evil");
        Assertions.assertThat(chatMessage.getSent()).isNotNull();
    }

}