package ru.veselov.websocketroomproject.websocket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.veselov.websocketroomproject.event.handler.UserDisconnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.RoomService;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketDisconnectListener {

    private final ChatUserService chatUserService;

    private final UserDisconnectEventHandler userDisconnectEventHandler;

    private final RoomService roomService;

    @EventListener
    public void handleUserDisconnect(SessionDisconnectEvent session) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();
        Optional<ChatUser> chatUserOptional = chatUserService.removeChatUser(sessionId);
        if (chatUserOptional.isPresent()) {
            ChatUser chatUser = chatUserOptional.get();
            userDisconnectEventHandler.handleDisconnectEvent(chatUser);
            roomService.removeUser(chatUser.getRoomId(), chatUser.getUsername());//TODO Test me
            log.info("[User {}] is disconnected", chatUser.getUsername());
        } else {
            log.info("[Session {}] was not connected, disconnect message after error", sessionId);
        }
    }

}