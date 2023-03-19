package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.UserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectListener {
    private final UserService userService;
    private final RoomService roomService;
    @EventListener
    public void handleUserConnection(SessionConnectEvent session){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(session.getMessage());
        String roomIdFromHeader = accessor.getFirstNativeHeader("roomId");

        //TODO when new user was connectoed shoulb be sent message to chat about it connection
    }
    @EventListener
    public void handleUserConnected(SessionConnectedEvent session){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(session.getMessage());
    }


}
