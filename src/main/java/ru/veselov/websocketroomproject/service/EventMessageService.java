package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.ChatUser;

public interface EventMessageService {

    void sendUserListToAllSubscriptions(String roomId);

    void sendUserConnectedMessage(ChatUser chatUser);

    void sendUserDisconnectedMessage(ChatUser chatUser);

    void sendUserListToSubscription(String roomId, String username);

}
