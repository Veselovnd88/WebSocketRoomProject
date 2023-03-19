package ru.veselov.websocketroomproject.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * Interceptor validates all Subscription commands for null destination
 * or not correct prefix
 */
@Slf4j
public class SocketSubscriptionInterceptor implements ChannelInterceptor {
    private final String destinationPrefix;

    public SocketSubscriptionInterceptor(String destinationPrefix) {
        this.destinationPrefix = destinationPrefix;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (!validate(accessor)) {
            throw new MessagingException("Destination cannot be null and should start with prefix [/topic]");
        }
        return message;
    }

    private boolean validate(StompHeaderAccessor accessor) {
        return isSubscribeCommand(accessor.getCommand()) && validateHeaders(accessor);
    }

    private boolean validateHeaders(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            log.warn("Destination is null");
            return false;
        }
        if (!destination.startsWith(destinationPrefix)) {
            log.warn("Destination has not correct prefix: {}", destination);
            return false;
        }
        return true;
    }

    private boolean isSubscribeCommand(StompCommand stompCommand) {
        return StompCommand.SUBSCRIBE.equals(stompCommand);
    }

}