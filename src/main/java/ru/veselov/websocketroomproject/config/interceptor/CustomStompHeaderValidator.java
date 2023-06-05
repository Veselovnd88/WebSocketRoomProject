package ru.veselov.websocketroomproject.config.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.security.AuthProperties;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomStompHeaderValidator {

    private final AuthProperties authProperties;

    public void validateAuthHeader(StompHeaderAccessor accessor) {
        if (!isValidAuthHeader(accessor)) {
            throw new IllegalArgumentException("Message should have Authorization header with valid prefix");
        }
    }

    public void validateRoomIdHeader(StompHeaderAccessor accessor) {
        if (!isValidRoomId(accessor)) {
            throw new IllegalArgumentException("Room Id should be integer value");
        }
    }

    private boolean isValidAuthHeader(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader(authProperties.getHeader());
        if (authHeader == null || !authHeader.startsWith(authProperties.getPrefix())) {
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