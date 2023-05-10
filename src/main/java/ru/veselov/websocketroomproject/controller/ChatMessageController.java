package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import ru.veselov.websocketroomproject.dto.request.ReceivedChatMessage;
import ru.veselov.websocketroomproject.service.ChatMessageService;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/{roomId}")
    public void processTextMessage(@DestinationVariable("roomId") String roomId,
                                   @Payload ReceivedChatMessage receivedChatMessage,
                                   Principal principal) {
        chatMessageService.sendToTopic(roomId, receivedChatMessage, principal);
    }

    @MessageMapping("/chat-private")
    public void processTextMessageToUser(@Payload ReceivedChatMessage receivedChatMessage,
                                         Principal principal) {
        String sentFrom = principal.getName();
        chatMessageService.sendToUser(receivedChatMessage, sentFrom);
    }

}