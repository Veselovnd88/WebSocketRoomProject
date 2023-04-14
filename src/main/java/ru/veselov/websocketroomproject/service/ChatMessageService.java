package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;

import java.security.Principal;

public interface ChatMessageService {

    void sendToTopic(String roomId, ReceivedChatMessage receivedChatMessage, Principal principal);

    void sendToUser(ReceivedChatMessage receivedChatMessage, Principal principal);

}
