package ru.veselov.websocketroomproject.config.interceptor;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.veselov.websocketroomproject.security.JWTUtils;

import java.security.Principal;
import java.util.Collections;

/**
 * Interceptor validates authentication and correct roomId
 */
@Slf4j
@RequiredArgsConstructor
public class SocketConnectionInterceptor implements ChannelInterceptor {

    private final JWTUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (isConnectCommand(accessor)) {
            String authorization = accessor.getFirstNativeHeader("Authorization");
            String jwt = authorization.substring(7);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            jwtUtils.getUsername(jwt),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(jwtUtils.getRole(jwt)))
                    );
            accessor.setUser(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String roomId = accessor.getFirstNativeHeader("roomId");
            if (!isValidRoomId(roomId)) {
                throw new MessagingException("Room Id should be integer value");
            }
        }
        return message;
    }

    private boolean validateAuthentication(Principal principal) {
        if (principal == null) {
            log.warn("No authenticated user in session");
            return false;
        }
        return true;
    }

    private boolean isValidRoomId(String roomId) {
        if (StringUtils.isBlank(roomId)) {
            log.warn("RoomId can't be null or empty");
            return false;
        }
        return true;
    }

    private boolean isConnectCommand(StompHeaderAccessor accessor) {
        return StompCommand.CONNECT.equals(accessor.getCommand());
    }

}