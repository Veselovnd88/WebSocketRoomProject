package ru.veselov.websocketroomproject.config.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Base64;

public class Base64EncoderInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.MESSAGE.equals(accessor.getCommand())) {
            String type = accessor.getFirstNativeHeader("content");

            if (StringUtils.equals("img", type)) {
                String payload = (String) message.getPayload();
                byte[] encodedPayload = Base64.getEncoder().encode(payload.getBytes());
                return MessageBuilder.createMessage(encodedPayload, message.getHeaders());
            }
            return message;
        }
        return message;
    }
}
