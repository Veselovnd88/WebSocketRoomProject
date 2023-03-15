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
        if (!validateAuthentication(session)) {
            return;
        }
        String username = session.getUser().getName();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(session.getMessage());
        if (!validateHeaders(headerAccessor)) {
            return;
        }
        User user = userService.findUserByUserName(username);
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        /*Additional header {roomId: roomNumber (integer)} should be added by FE in subscribe function*/
        Integer roomId = Integer.valueOf((String) headerAccessor.getHeader("roomId"));
        Room roomById = roomService.findRoomById(roomId);
        boolean isOwner = roomById.getOwner().getUsername().equals(user.getUsername());
        ChatUser chatUser = new ChatUser(user.getId(), roomId, sessionId, username, destination, isOwner);
        messagingTemplate.convertAndSend(destination, chatUserMapper.chatUserToDTO(chatUser));
        log.info("User {}, id {}, with session {} connected to topic {} of room #{}", user.getUsername(), user.getId(), sessionId, destination, roomId);
    }

    private Boolean validateAuthentication(SessionSubscribeEvent session) {
        if (session.getUser() == null) {
            log.error("No authenticated user in session");
            return false;
        } else return true;
    }

    private Boolean validateHeaders(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            log.error("Topic is null");
            return false;
        }
        if (accessor.getHeader("roomId") == null) {
            log.error("RoomId is null");
            return false;
        }
        try {
            Integer roomId = Integer.valueOf((String) accessor.getHeader("roomId"));
        } catch (NumberFormatException e) {
            log.error("RoomId is not int value");
            return false;
        }
        if (!accessor.getDestination().startsWith(usersTopic)) {
            log.trace("Not correct topic for answer: [topic] {}", destination);
            return false;
        }
        return true;
    }
}