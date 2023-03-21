package ru.veselov.websocketroomproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.ChatUser;

@Service
@Slf4j
public class ChatUserService {

    public void saveChatUser(ChatUser chatUser) {
        log.info("ChatUser {} saved to db", chatUser.getUsername());
        /*Stub for further implementation*/
    }

    public ChatUser getChatUserBySessionId(String sessionId) {
        log.info("Retrieving ChatUser with sessionId: {}", sessionId);
        return new ChatUser(
                "test",
                "5",
                sessionId
        );
        /*stub for further implementation*/
    }
}
