package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;

/**
 * After confirmation of connection from server send message about
 * connected user to messages topic to notify
 */
//@Component don't need
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectedListener {

    private final ChatUserService chatUserService;

    private final EventMessageService eventMessageService;

    @EventListener
    public void handleConnectedUserEvent(SessionConnectedEvent session) {
        log.warn("in connected");
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();
        /*ChatUser chatUser = chatUserService.findChatUserBySessionId(sessionId);
        eventMessageService.sendUserListToAllSubscriptions(chatUser.getRoomId());
        eventMessageService.sendUserConnectedMessageToAll(chatUser);*/
        log.info("User {} is connected", sessionId);
    }

}