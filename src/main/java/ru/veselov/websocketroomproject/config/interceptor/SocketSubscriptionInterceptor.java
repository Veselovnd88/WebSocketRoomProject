package ru.veselov.websocketroomproject.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * Interceptor validates all Subscription commands for null destination
 * or not correct prefix
 */
@Slf4j
@Component
public class SocketSubscriptionInterceptor implements ChannelInterceptor {
    @Value("${socket.dest-prefix}")
    private String destinationPrefix;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = accessor.getCommand();
        if (isSubscribeCommand(stompCommand)) {
            if (!validateHeaders(accessor)) {
                throw new MessagingException("Destination cannot be null and should start with prefix [/topic]");
            }
        }
        return message;
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