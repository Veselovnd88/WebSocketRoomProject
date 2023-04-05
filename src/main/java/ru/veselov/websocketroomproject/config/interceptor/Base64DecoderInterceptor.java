package ru.veselov.websocketroomproject.config.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Base64;

public class Base64DecoderInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.MESSAGE.equals(accessor.getCommand())) {
            String type = accessor.getFirstNativeHeader("content");

            if(StringUtils.equals("img",type)){
                byte[] payload = (byte[]) message.getPayload();
                String decodedPayload = new String(Base64.getDecoder().decode(payload));
                return MessageBuilder.createMessage(decodedPayload, message.getHeaders());
            }
            return message;
        }
        return message;

    }
}
