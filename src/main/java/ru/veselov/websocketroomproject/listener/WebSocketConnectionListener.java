package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;

/**
 * Handling CONNECTION command from FrontEnd
 * placing ChatUser object with required information to the Cache/Redis
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectionListener {

    @Value("${socket.header-room-id}")
    private String roomIdHeader;

    private final ChatUserService chatUserService;

    private final EventMessageService eventMessageService;

    @EventListener
    public void handleUserConnection(SessionConnectEvent session) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(session.getMessage());
        String roomId = accessor.getFirstNativeHeader(roomIdHeader);
        String username = session.getUser().getName();
        String sessionId = accessor.getSessionId();
        ChatUser chatUser = new ChatUser(
                username,
                roomId,
                sessionId
        );
        chatUserService.saveChatUser(chatUser);
        eventMessageService.sendUserListToAllSubscriptions(chatUser.getRoomId());
        eventMessageService.sendUserConnectedMessageToAll(chatUser);
        log.info("User {} is connected to room # {}", chatUser.getUsername(), chatUser.getSession());
    }

}