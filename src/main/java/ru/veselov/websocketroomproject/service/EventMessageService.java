package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.ChatUser;

public interface EventMessageService {

    void sendUserListToAllSubscriptions(String roomId);

    void sendUserConnectedMessageToAll(ChatUser chatUser);

    void sendUserDisconnectedMessageToAll(ChatUser chatUser);

}
