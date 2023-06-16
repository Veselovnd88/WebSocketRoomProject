package ru.veselov.websocketroomproject.websocket.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

@RequiredArgsConstructor
@Slf4j
public class SocketJwtStompHeaderInterceptor implements ChannelInterceptor {

    private final AuthProperties authProperties;

    private final CustomStompHeaderValidator customStompHeaderValidator;

    private final JwtAuthenticationManager authenticationManager;

    private final JwtValidator jwtValidator;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
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
