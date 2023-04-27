package ru.veselov.websocketroomproject.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import ru.veselov.websocketroomproject.dto.PlayerStateDTO;
import ru.veselov.websocketroomproject.service.PlayerStateMessageService;

import java.security.Principal;

@SpringBootTest
class PlayerStateMessageServiceImplTest {

    private static final String ROOM_ID = "5";

    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    PlayerStateMessageService playerStateMessageService;

    @Test
    void shouldSendMessageToYoutubeTopic() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("user1");
        PlayerStateDTO playerStateDTO = new PlayerStateDTO(1, "111.111", "low", "1");

        playerStateMessageService.sendToTopic(ROOM_ID, playerStateDTO);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(PlayerStateDTO.class));
    }

}