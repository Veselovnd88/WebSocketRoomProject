package ru.veselov.websocketroomproject.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Arrays;

/**
 * Interceptor validates all Subscription commands for null destination
 * or not correct prefix
 */
@Slf4j
public class SocketSubscriptionInterceptor implements ChannelInterceptor {

    private final String[] destinationPrefixes;

    private final String userPrefix;

    public SocketSubscriptionInterceptor(String[] destinationPrefixes, String userPrefix) {
        this.destinationPrefixes = destinationPrefixes;
        this.userPrefix = userPrefix;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand stompCommand = accessor.getCommand();
        if (isSubscribeCommand(stompCommand)) {
            if (!validateHeaders(accessor)) {
                throw new MessagingException("Destination cannot be null and should start with prefixes: "
                        + Arrays.toString(destinationPrefixes));
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

        if (!isPrefixExists(destination)) {
            log.warn("Destination has not correct prefix: {}", destination);
            return false;
        }
        return true;
    }

    private boolean isSubscribeCommand(StompCommand stompCommand) {
        return StompCommand.SUBSCRIBE.equals(stompCommand);
    }

    private boolean isPrefixExists(String destination) {
        if (destination.startsWith(userPrefix)) {
            return true;
        }
        return Arrays.stream(destinationPrefixes).anyMatch(destination::startsWith);
    }

}