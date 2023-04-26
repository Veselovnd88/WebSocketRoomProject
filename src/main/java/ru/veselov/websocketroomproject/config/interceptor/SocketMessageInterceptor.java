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
import ru.veselov.websocketroomproject.security.AuthTokenManager;
import ru.veselov.websocketroomproject.security.JWTAuthToken;
import ru.veselov.websocketroomproject.security.JWTProperties;

@Slf4j
@RequiredArgsConstructor
public class SocketMessageInterceptor implements ChannelInterceptor {

    private final JWTProperties jwtProperties;

    private final CustomStompHeaderValidator customStompHeaderValidator;

    private final AuthTokenManager authTokenManager;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.SEND) {
            customStompHeaderValidator.validateAuthHeader(accessor);
            String authHeader = accessor.getFirstNativeHeader(jwtProperties.getHeader());
            JWTAuthToken token = authTokenManager.createToken(authHeader);//checked in validator
            accessor.setUser(token);
            authTokenManager.setAuthentication(token);
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

}