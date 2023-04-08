package ru.veselov.websocketroomproject.event;

import ru.veselov.websocketroomproject.model.ChatUser;

public interface UserDisconnectEventHandler {

    void handleDisconnectEvent(ChatUser chatUser);
}
