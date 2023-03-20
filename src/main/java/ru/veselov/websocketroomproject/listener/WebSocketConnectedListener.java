package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectedListener {
    @Value("${socket.chat-topic}")
    private String chatDestination;

    private final ChatUserService chatUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handleConnectedUserEvent(SessionConnectedEvent sessionConnectedEvent) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(sessionConnectedEvent.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();
        ChatUser chatUser = chatUserService.getChatUserBySessionId(sessionId);
        simpMessagingTemplate.convertAndSend(chatDestination + "/" + chatUser.getRoomId(),
                "User connected to" + chatDestination + "/" + chatUser.getRoomId());

        log.info("Send message about connected user {} to all current users", chatUser.getUsername());
    }

}
