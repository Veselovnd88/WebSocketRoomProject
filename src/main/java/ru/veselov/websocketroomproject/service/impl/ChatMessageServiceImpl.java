package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;
import ru.veselov.websocketroomproject.mapper.ChatMessageMapper;
import ru.veselov.websocketroomproject.service.ChatMessageService;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    @Value("${socket.private-message-topic}")
    private String privateMessageDestination;

    @Value("${socket.chat-topic}")
    private String chatDestination;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatMessageMapper chatMessageMapper;


    @Override
    public void sendToTopic(String roomId, ReceivedChatMessage receivedChatMessage, Principal principal) {
        String username = principal.getName();
        simpMessagingTemplate.convertAndSend(
                toDestination(roomId),
                createSendChatMessage(receivedChatMessage, username)
        );
        log.info("Message sent to topic {}", toDestination(roomId));
    }

    @Override
    public void sendToUser(ReceivedChatMessage receivedChatMessage, Principal principal) {
        String sendTo = receivedChatMessage.getSendTo();
        String username = principal.getName();
        simpMessagingTemplate.convertAndSendToUser(sendTo,
                privateMessageDestination,
                createSendChatMessage(receivedChatMessage, username)
        );
        log.info("Private message sent to user {}", sendTo);
    }

    private SendChatMessage createSendChatMessage(ReceivedChatMessage receivedChatMessage, String username) {
        SendChatMessage sendChatMessage = chatMessageMapper.toSendChatMessage(receivedChatMessage);
        sendChatMessage.setSentFrom(username);
        return sendChatMessage;
    }

    private String toDestination(String roomId) {
        return chatDestination + "/" + roomId;
    }

}