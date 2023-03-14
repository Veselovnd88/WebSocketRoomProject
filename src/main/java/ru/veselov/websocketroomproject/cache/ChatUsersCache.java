package ru.veselov.websocketroomproject.cache;



import ru.veselov.websocketroomproject.model.ChatUser;

import java.util.Map;

public interface ChatUsersCache extends Cache {
    void addUser(Integer roomId, ChatUser chatUser);
    void removeUser(Integer roomId, String sessionId);

    Map<String,ChatUser> getRoomUsers(Integer roomId);

    void removeRoom(Integer roomId);


}

