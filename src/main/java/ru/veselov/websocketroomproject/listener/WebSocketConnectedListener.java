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
import ru.veselov.websocketroomproject.service.SubscriptionService;

/**
 * After confirmation of connection from server send message about
 * connected user to messages topic to notify
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectedListener {

    private final ChatUserService chatUserService;

    private final EventMessageService eventMessageService;

    private final SubscriptionService subscriptionService;

    @EventListener
    public void handleConnectedUserEvent(SessionConnectedEvent session) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();
        ChatUser chatUser = chatUserService.findChatUserBySessionId(sessionId);
        setSubscriptionConnected(chatUser);
        eventMessageService.sendUserConnectedMessage(chatUser);
        eventMessageService.sendUserListToAllSubscriptions(chatUser.getRoomId());
        log.info("User {} is connected", chatUser.getUsername());
    }

    private void setSubscriptionConnected(ChatUser chatUser) {
        subscriptionService.findSubscription(chatUser.getRoomId(), chatUser.getUsername()).setConnected(true);
    }

}