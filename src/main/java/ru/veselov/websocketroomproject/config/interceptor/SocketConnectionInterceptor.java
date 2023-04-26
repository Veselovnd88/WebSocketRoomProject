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

    private final CustomStompHeaderValidator customStompHeaderValidator;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (isConnectCommand(accessor)) {
            customStompHeaderValidator.validateRoomIdHeader(accessor);
        }
        return message;
    }

    private boolean isConnectCommand(StompHeaderAccessor accessor) {
        return StompCommand.CONNECT.equals(accessor.getCommand());
    }

}