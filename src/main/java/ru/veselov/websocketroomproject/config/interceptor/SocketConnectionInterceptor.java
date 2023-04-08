package ru.veselov.websocketroomproject.config.interceptor;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * Interceptor validates authentication and correct roomId
 */
@Slf4j
@Component
public class SocketConnectionInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (isConnectCommand(accessor)) {
            Principal principal = accessor.getUser();
            if (!validateAuthentication(principal)) {
                throw new MessagingException("No permission for no authenticated user");
            }
            String roomId = accessor.getFirstNativeHeader("roomId");
            if (!isValidRoomId(roomId)) {
                throw new MessagingException("Room Id should be integer value");
            }
        }
        return message;
    }

    private boolean validateAuthentication(Principal principal) {
        if (principal == null) {
            log.warn("No authenticated user in session");
            return false;
        }
        return true;
    }

    private boolean isValidRoomId(String roomId) {
        if (StringUtils.isBlank(roomId)) {
            log.warn("RoomId can't be null or empty");
            return false;
        }
        return true;
    }

    private boolean isConnectCommand(StompHeaderAccessor accessor) {
        return StompCommand.CONNECT.equals(accessor.getCommand());
    }

}