package ru.veselov.websocketroomproject.websocket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.veselov.websocketroomproject.event.UserDisconnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketDisconnectListener {

    private final ChatUserService chatUserService;

    private final UserDisconnectEventHandler userDisconnectEventHandler;

    @EventListener
    public void handleUserDisconnect(SessionDisconnectEvent session) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();
        ChatUser chatUser = chatUserService.removeChatUser(sessionId);
        userDisconnectEventHandler.handleDisconnectEvent(chatUser);
        log.info("[User {}] is disconnected", chatUser.getUsername());
    }

}