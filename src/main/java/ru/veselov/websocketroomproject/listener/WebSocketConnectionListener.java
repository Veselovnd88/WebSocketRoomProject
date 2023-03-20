package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import ru.veselov.websocketroomproject.model.ChatUser;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectionListener {
    @Value("${socket.chat-topic}")
    private String chatDestination;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handleUserConnection(SessionConnectEvent session) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(session.getMessage());
        String roomId = accessor.getFirstNativeHeader("roomId");
        String username = session.getUser().getName();
        String sessionId = accessor.getSessionId();
        ChatUser chatUser = new ChatUser(
                roomId,
                username,
                sessionId,
                LocalDateTime.now()
        );
        log.info("User {} connecting to room # {}, placed in cache",
                chatUser.getUsername(), chatUser.getSession());
        simpMessagingTemplate.convertAndSend(chatDestination + "/" + roomId,
                "User " + chatUser.getUsername() + " connected to chat");
    }

}
