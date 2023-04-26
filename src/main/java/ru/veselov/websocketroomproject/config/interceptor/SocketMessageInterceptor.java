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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.veselov.websocketroomproject.security.JWTFilter;
import ru.veselov.websocketroomproject.security.JWTUtils;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class SocketMessageInterceptor implements ChannelInterceptor {

    private final JWTUtils jwtUtils;

    private final CustomStompHeaderValidator customStompHeaderValidator;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.SEND) {
          //  customStompHeaderValidator.validate(accessor);
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
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }


}