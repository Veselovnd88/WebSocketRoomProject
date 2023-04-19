package ru.veselov.websocketroomproject.event;

import ru.veselov.websocketroomproject.model.ChatUser;

public interface UserConnectEventHandler {

    void handleConnectEvent(ChatUser chatUser);
}
