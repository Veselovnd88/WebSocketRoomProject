package ru.veselov.websocketroomproject.websocket.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.veselov.websocketroomproject.exception.InvalidStompHeaderException;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.authentication.JwtAuthenticationToken;
import ru.veselov.websocketroomproject.security.jwt.JwtValidator;
import ru.veselov.websocketroomproject.security.managers.JwtAuthenticationManager;

/**
 * This interceptor validates authorization header in STOMP header, and check jwt to place it to the SecurityContext
 * for sending private messages and having access to Authentication object in the controller
 * Authorization header with Bearer Jwt should be in STOMP header of CONNECT and SEND commands
 * CONNECT -> for checking user before saving Chat User in Cache
 * SEND -> for placing authentication to SecurityContext
 */
@RequiredArgsConstructor
@Slf4j
public class SocketJwtStompHeaderCheckInterceptor implements ChannelInterceptor {

    private final AuthProperties authProperties;

    private final CustomStompHeaderValidator customStompHeaderValidator;

    private final JwtAuthenticationManager authenticationManager;

    private final JwtValidator jwtValidator;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (isCommandSendOrConnect(accessor)) {
            customStompHeaderValidator.validateAuthHeader(accessor);
            String authHeader = accessor.getFirstNativeHeader(authProperties.getHeader());
            String jwt = authHeader.substring(authProperties.getPrefix().length());//checked in validator
            if (StringUtils.isNotBlank(jwt) && jwtValidator.isValidJwt(jwt)) {
                JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
                Authentication authentication = authenticationManager.authenticate(token);
                if (authentication.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);
                    return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                }
            }
            throw new InvalidStompHeaderException("Invalid Jwt in Stomp header");
        }
        return message;

    }

    private boolean isCommandSendOrConnect(StompHeaderAccessor accessor) {
        return (accessor.getCommand() == StompCommand.SEND || accessor.getCommand() == StompCommand.CONNECT);
    }

}
