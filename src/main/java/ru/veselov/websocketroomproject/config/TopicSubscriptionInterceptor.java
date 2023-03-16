package ru.veselov.websocketroomproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Slf4j
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Principal principal = accessor.getUser();
            if (!validateAuthentication(principal)) {
                throw new MessagingException("No permission for no authenticated user");
            }
            if (!validateHeaders(accessor)) {
                throw new MessagingException("Topic cannot be null or start with prefix [/topic]");
            }
        }
        return message;
    }


    private boolean validateAuthentication(Principal principal) {
        if (principal == null) {
            log.error("No authenticated user in session");
            return false;
        }
        return true;
    }

    private boolean validateHeaders(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            log.error("Topic is null");
            return false;
        }
        if(!destination.startsWith("/topic")){
            return false;
        }
        return true;
    }
}
