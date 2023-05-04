package ru.veselov.websocketroomproject;

import ru.veselov.websocketroomproject.entity.ChatUserEntity;

public class ChatUserUtils {

    public static ChatUserEntity getChatUser(String roomId, String username, String session) {
        ChatUserEntity chatUser = new ChatUserEntity();
        chatUser.setRoomId(roomId);
        chatUser.setUsername(username);
        chatUser.setSession(session);
        return chatUser;
    }

}