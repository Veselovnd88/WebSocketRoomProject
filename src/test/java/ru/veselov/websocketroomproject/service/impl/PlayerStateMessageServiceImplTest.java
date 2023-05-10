package ru.veselov.websocketroomproject.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.veselov.websocketroomproject.dto.PlayerStateDTO;

@ExtendWith(MockitoExtension.class)
class PlayerStateMessageServiceImplTest {

    private static final String ROOM_ID = "5";

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    PlayerStateMessageServiceImpl playerStateMessageService;

    @Test
    void shouldSendMessageToYoutubeTopic() {
        PlayerStateDTO playerStateDTO = new PlayerStateDTO(1, "111.111", "low", "1");

        playerStateMessageService.sendToTopic(ROOM_ID, playerStateDTO);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(PlayerStateDTO.class));
    }

}