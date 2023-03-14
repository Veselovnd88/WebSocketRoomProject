package ru.veselov.websocketroomproject.listener;


import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.model.RoomModel;
import ru.veselov.websocketroomproject.model.UserModel;
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
        UserModel userModel = userService.findUserByUserName(username);
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        Integer roomId;
        try {
            roomId = getRoomId(destination);
        } catch (NumberFormatException e) {
            log.error("Not correct room number in destination {}, {}", destination, e.getMessage());
            return;
        }
        RoomModel roomById = roomService.findRoomById(roomId);
        boolean isOwner = roomById.getOwner().getUsername().equals(userModel.getUsername());
        ChatUser chatUser = new ChatUser(
                userModel.getId(),
                roomId,
                sessionId,
                username,
                destination,
                isOwner
        );
        messagingTemplate.convertAndSend(destination, ChatUserDTO.convertToChatUserDTO(chatUser));
        log.info("User {}, id {}, with session {} connected to topic {} of room #{}",
                userModel.getUsername(), userModel.getId(), sessionId, destination, roomId);
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
        if (!accessor.getDestination().startsWith(usersTopic)) {
            log.trace("Not correct topic for answer: [topic] {}", destination);
            return false;
        }
        return true;
    }

    private Integer getRoomId(String destination) {
        String[] split;
        split = destination.split("/");
        return Integer.valueOf(split[split.length - 1]);
    }

}
