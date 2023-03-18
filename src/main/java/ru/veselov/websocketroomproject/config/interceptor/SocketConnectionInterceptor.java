package ru.veselov.websocketroomproject.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.security.Principal;

/**
 * Interceptor validates authentication and correct roomId
 */
@Slf4j
public class SocketConnectionInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Principal principal = accessor.getUser();
            if (!validateAuthentication(principal)) {
                throw new MessagingException("No permission for no authenticated user");
            }
            String roomId = accessor.getFirstNativeHeader("roomId");
            if (!validateRoomIdInHeader(roomId)) {
                throw new MessagingException("Room Id should be integer value");
            }
        }
        return message;
    }

    private boolean validateAuthentication(Principal principal) {
        if (principal == null) {
            log.info("No authenticated user in session");
            return false;
        }
        return true;
    }

    private boolean validateRoomIdInHeader(String roomId) {
        if (roomId == null) {
            log.info("RoomId is null");
            return false;
        }
        if (roomId.isEmpty()) {
            log.info("RoomId is empty");
            return false;
        }
        try {
            Integer.valueOf(roomId);
        } catch (NumberFormatException e) {
            log.info("RoomId is not integer value");
            return false;
        }
        return true;
    }

}