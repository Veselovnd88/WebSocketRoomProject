package ru.veselov.websocketroomproject.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class SocketMessageInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.SEND) {
            String firstNativeHeader = accessor.getFirstNativeHeader("content-type");
            if (firstNativeHeader != null) {
                if (firstNativeHeader.equals("application/octet-stream")) {
                    String originalDestination = accessor.getDestination();
                    String newDestination = originalDestination + "/binary";
                    accessor.setDestination(newDestination);
                    Message<?> newMessage = MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                    return newMessage;

                }
            }
        }

        return message;
    }
}
