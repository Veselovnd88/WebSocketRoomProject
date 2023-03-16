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

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectionChatListener {
    @Value("${socket.users-topic}")
    private String usersTopic;
    private final UserService userService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatUserMapper chatUserMapper;

    @EventListener
    public void handleSubscribeUser(SessionSubscribeEvent session) {
        String username = session.getUser().getName();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(session.getMessage());
        if (!validateHeaders(headerAccessor)) {
            return;
        }
        User user = userService.findUserByUserName(username);
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        /*Additional header {roomId: roomNumber (integer)} should be added by FE in subscribe function*/
        Integer roomId = Integer.valueOf(headerAccessor.getFirstNativeHeader("roomId"));
        Room roomById = roomService.findRoomById(roomId);
        boolean isOwner = roomById.getOwner().getUsername().equals(user.getUsername());
        ChatUser chatUser = new ChatUser(user.getId(), roomId, sessionId, username, destination, isOwner);
        messagingTemplate.convertAndSend(destination, chatUserMapper.chatUserToDTO(chatUser));
        log.info("User {}, id {}, with session {} connected to topic {} of room #{}", user.getUsername(), user.getId(), sessionId, destination, roomId);
    }

    private boolean validateHeaders(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (!accessor.getDestination().startsWith(usersTopic)) {
            log.trace("Not correct topic for answer: [topic] {}", destination);
            return false;
        }
        return true;
    }
}