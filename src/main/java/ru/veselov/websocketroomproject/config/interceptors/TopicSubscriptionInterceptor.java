package ru.veselov.websocketroomproject.config.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    private final String destinationPrefix;

    public TopicSubscriptionInterceptor(String destinationPrefix) {
        this.destinationPrefix = destinationPrefix;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && (!validateHeaders(accessor))) {
            throw new MessagingException("Destination cannot be null or start with prefix [/topic]");
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
