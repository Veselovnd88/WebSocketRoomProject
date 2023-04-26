package ru.veselov.websocketroomproject.config.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.veselov.websocketroomproject.security.AuthTokenManager;
import ru.veselov.websocketroomproject.security.JWTAuthToken;
import ru.veselov.websocketroomproject.security.JWTFilter;
import ru.veselov.websocketroomproject.security.JWTUtils;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class SocketMessageInterceptor implements ChannelInterceptor {

    private static final String BEARER = "Bearer ";

    private static final String HEADER = "Authorization";

    private final JWTUtils jwtUtils;

    private final CustomStompHeaderValidator customStompHeaderValidator;

    private final AuthTokenManager authTokenManager;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.SEND) {
            customStompHeaderValidator.validateAuthHeader(accessor);
            String authHeader = accessor.getFirstNativeHeader(HEADER);
            if (authHeader == null || !authHeader.startsWith(BEARER)) {
                throw new MessagingException("Not correct JWT in auth");
            }
            JWTAuthToken token = authTokenManager.createToken(authHeader);
            accessor.setUser(token);
            authTokenManager.setAuthentication(token);
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }


}