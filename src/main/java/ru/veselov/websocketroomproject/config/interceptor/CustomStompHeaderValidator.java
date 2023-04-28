package ru.veselov.websocketroomproject.config.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.security.JwtProperties;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomStompHeaderValidator {

    private final JwtProperties jwtProperties;

    public void validateAuthHeader(StompHeaderAccessor accessor) {
        if (!isValidAuthHeader(accessor)) {
            throw new MessagingException("Message should have Authorization header with valid prefix");
        }
    }

    public void validateRoomIdHeader(StompHeaderAccessor accessor) {
        if (!isValidRoomId(accessor)) {
            throw new MessagingException("Room Id should be integer value");
        }
    }

    private boolean isValidAuthHeader(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader(jwtProperties.getHeader());
        if (authHeader == null || !authHeader.startsWith(jwtProperties.getPrefix())) {
            log.warn("AuthHeader is null or doesn't start with correct prefix: [{}]", authHeader);
            return false;
        }
        return true;
    }

    private boolean isValidRoomId(StompHeaderAccessor accessor) {
        String roomId = accessor.getFirstNativeHeader("roomId");
        if (StringUtils.isBlank(roomId)) {
            log.warn("RoomId can't be null or empty: [{}]", roomId);
            return false;
        }
        return true;
    }
}