package ru.veselov.websocketroomproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.ChatUser;

import java.time.LocalDateTime;

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
                "5",
                sessionId,
                "test",
                LocalDateTime.now()
        );
        /*stub for further implementation*/
    }
}
