package ru.veselov.websocketroomproject.config.interceptor;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * Interceptor validates headers in connect messages
 */
@Slf4j
@RequiredArgsConstructor
public class SocketConnectionInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (isConnectCommand(accessor)) {
            if (!isValidRoomId(accessor)) {
                throw new MessagingException("Room Id should be integer value");
            }
            if (!isValidAuthHeader(accessor)) {
                throw new MessagingException("Message should have Authorization header");
            }
        }

        return message;
    }

    private boolean isValidAuthHeader(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null) {
            log.warn("No authHeader in message");
            return false;
        }
        return true;
    }

    private boolean isValidRoomId(StompHeaderAccessor accessor) {
        String roomId = accessor.getFirstNativeHeader("roomId");
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