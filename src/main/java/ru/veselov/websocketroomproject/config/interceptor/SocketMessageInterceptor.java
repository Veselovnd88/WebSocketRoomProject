package ru.veselov.websocketroomproject.config.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.authentication.JwtAuthenticationToken;
import ru.veselov.websocketroomproject.security.managers.JwtAuthenticationManager;

@Slf4j
@RequiredArgsConstructor
public class SocketMessageInterceptor implements ChannelInterceptor {

    private final AuthProperties authProperties;

    private final CustomStompHeaderValidator customStompHeaderValidator;

    private final JwtAuthenticationManager authenticationManager;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.SEND) {
            customStompHeaderValidator.validateAuthHeader(accessor);
            String authHeader = accessor.getFirstNativeHeader(authProperties.getHeader());
            String jwt = authHeader.substring(authProperties.getPrefix().length());//checked in validator
            JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
            Authentication authentication = authenticationManager.authenticate(token);
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                accessor.setUser(token);
                return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
            }
        }
        return message;
    }

}
