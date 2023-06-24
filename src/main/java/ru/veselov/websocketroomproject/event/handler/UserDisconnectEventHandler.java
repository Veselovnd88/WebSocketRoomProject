package ru.veselov.websocketroomproject.event.handler;

import ru.veselov.websocketroomproject.model.ChatUser;

public interface UserDisconnectEventHandler {

    void handleDisconnectEvent(ChatUser chatUser);
}
