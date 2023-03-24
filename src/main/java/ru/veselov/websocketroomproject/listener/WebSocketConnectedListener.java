package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.SendMessageDTO;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

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

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatUserMapper chatUserMapper;

    @EventListener
    public void handleConnectedUserEvent(SessionConnectedEvent session) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();
        ChatUser chatUser = chatUserService.findChatUserBySessionId(sessionId);
        simpMessagingTemplate.convertAndSend(
                toDestination(chatUser),
                toPayload(chatUser)
        );
        log.info("User {} is connected", chatUser.getUsername());
    }

    private String toDestination(ChatUser chatUser) {
        return chatDestination + "/" + chatUser.getRoomId();
    }

    private SendMessageDTO<ChatUserDTO> toPayload(ChatUser chatUser) {
        return new SendMessageDTO<>(chatUserMapper.chatUserToDTO(chatUser));
    }

}