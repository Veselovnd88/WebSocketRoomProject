package ru.veselov.websocketroomproject.config.interceptors;

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
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && (!validateHeaders(accessor))) {
            throw new MessagingException("Destination cannot be null and should start with prefix [/topic]");
        }
        return message;
    }

    private boolean validateHeaders(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            log.error("Destination is null");
            return false;
        }
        if (!destination.startsWith(destinationPrefix)) {
            log.error("Destination has not correct prefix: {}", destination);
            return false;
        }
        return true;
    }

}