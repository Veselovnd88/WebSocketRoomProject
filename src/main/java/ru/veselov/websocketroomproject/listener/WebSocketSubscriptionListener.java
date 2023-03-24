package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.SendMessageDTO;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.List;
import java.util.Set;

/**
 * Handling subscriptions.
 * Filter to the chosen destination that we need to send message, here - topic with users.
 * Then find all users from room and send list to the topic
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSubscriptionListener {

    @Value("${socket.users-topic}")
    private String usersTopic;

    @Value("${socket.header-room-id}")
    private String roomIdHeader;

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatUserService chatUserService;

    private final ChatUserMapper chatUserMapper;

    @EventListener
    public void handleUserSubscription(SessionSubscribeEvent session) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(session.getMessage());
        if (filterDestination(headerAccessor)) {
            return;
        }
        String destination = headerAccessor.getDestination();
        String roomId = headerAccessor.getFirstNativeHeader(roomIdHeader);
        messagingTemplate.convertAndSend(
                destination,
                toUserListPayload(roomId)
        );
        log.info("Updated userlist of room {}", roomId);
    }

    private boolean filterDestination(StompHeaderAccessor accessor) {
        return !accessor.getDestination().startsWith(usersTopic);
    }

    private SendMessageDTO<List<ChatUserDTO>> toUserListPayload(String roomId) {
        Set<ChatUser> chatUsersByRoomId = chatUserService.findChatUsersByRoomId(roomId);
        List<ChatUserDTO> chatUserDTOS = chatUsersByRoomId.stream().map(chatUserMapper::chatUserToDTO).toList();
        return new SendMessageDTO<>(chatUserDTOS);
    }

}