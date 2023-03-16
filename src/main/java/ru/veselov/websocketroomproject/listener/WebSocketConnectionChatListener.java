package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.User;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.UserService;

import java.security.Principal;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectionChatListener {
    @Value("${socket.users-topic}")
    private String usersTopic;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handling subscriptions.
     * Filter to the chosen destination that we need to send message, here - topic with users.
     * Create ChatUser object, map it to DTO and send to the topic.
     */
    @EventListener
    public void handleUserSubscription(SessionSubscribeEvent session) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(session.getMessage());
        if (filterDestination(headerAccessor)) {
            return;
        }
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        messagingTemplate.convertAndSend(destination, sessionId);
        /*User and room data should be retrieved from cache, this information should be added to cache
         * in connectHandling listener or in controller (will be implemented later)*/
        log.info("User [username], id [id], with session {} connected to topic {} of room #[roomId]",
                sessionId, destination);
    }

    private boolean filterDestination(StompHeaderAccessor accessor) {
        return !accessor.getDestination().startsWith(usersTopic);
    }
}