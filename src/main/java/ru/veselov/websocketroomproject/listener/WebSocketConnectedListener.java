package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.veselov.websocketroomproject.controller.EventType;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.SendMessageDTO;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;

/**
 * After confirmation of connection from server send message about
 * connected user to messages topic to notify
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectedListener {

    @Value("${socket.chat-topic}")
    private String chatDestination;

    private final ChatUserService chatUserService;

    private final EventMessageService eventMessageService;

    private final ChatUserMapper chatUserMapper;

    @EventListener
    public void handleConnectedUserEvent(SessionConnectedEvent session) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();
        ChatUser chatUser = chatUserService.findChatUserBySessionId(sessionId);
        log.info("User {} is connected", chatUser.getUsername());
        eventMessageService.sendUserConnectedMessage(chatUser);
        eventMessageService.sendUserList(chatUser.getRoomId());
    }

}