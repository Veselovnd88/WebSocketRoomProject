package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import ru.veselov.websocketroomproject.event.UserConnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.security.JWTProperties;
import ru.veselov.websocketroomproject.security.JWTUtils;
import ru.veselov.websocketroomproject.service.ChatUserService;

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

    private final UserConnectEventHandler userConnectEventHandler;

    private final ChatUserService chatUserService;

    private final JWTUtils jwtUtils;

    private final JWTProperties jwtProperties;

    @EventListener
    public void handleUserConnection(SessionConnectEvent session) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(session.getMessage());
        String roomId = accessor.getFirstNativeHeader(roomIdHeader);
        String sessionId = accessor.getSessionId();
        ChatUser chatUser = new ChatUser(
                getUsernameFromHeader(accessor),
                roomId,
                sessionId
        );
        chatUserService.saveChatUser(chatUser);
        userConnectEventHandler.handleConnectEvent(chatUser);
        log.info("[User {} with session {}] is connected to room", chatUser.getUsername(), chatUser.getSession());
    }

    private String getUsernameFromHeader(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(jwtProperties.getHeader());
        return jwtUtils.getUsername(authorization.substring(7));//checked before connection
    }

}