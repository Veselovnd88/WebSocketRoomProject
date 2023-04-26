package ru.veselov.websocketroomproject.config.interceptor;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class CustomStompHeaderValidator {


    public void validate(StompHeaderAccessor accessor) {
        if (!isValidRoomId(accessor)) {
            throw new MessagingException("Room Id should be integer value");
        }
        if (!isValidAuthHeader(accessor)) {
            throw new MessagingException("Message should have Authorization header");
        }
    }

    private boolean isValidAuthHeader(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null) {
            log.warn("No authHeader in message");
            return false;
        }
        return true;
    }

    private boolean isValidRoomId(StompHeaderAccessor accessor) {
        String roomId = accessor.getFirstNativeHeader("roomId");
        if (StringUtils.isBlank(roomId)) {
            log.warn("RoomId can't be null or empty");
            return false;
        }
        return true;
    }
}
