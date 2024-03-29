package ru.veselov.websocketroomproject.websocket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import ru.veselov.websocketroomproject.event.handler.UserConnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.jwt.JwtHelper;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.RoomService;

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

    private final JwtHelper jwtHelper;

    private final AuthProperties authProperties;

    private final RoomService roomService;

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
        roomService.addUserCount(roomId, chatUser.getUsername());
        log.info("[User {} with session {}] is connected to room", chatUser.getUsername(), chatUser.getSession());
    }

    private String getUsernameFromHeader(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(authProperties.getHeader());
        return jwtHelper.getUsername(authorization.substring(7));//checked before connection
    }

}
