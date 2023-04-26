package ru.veselov.websocketroomproject.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.config.interceptor.CustomStompHeaderValidator;

import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTSocketFilter {

    private static final String BEARER = "Bearer ";

    private static final String HEADER = "Authorization";

    private final JWTUtils jwtUtils;

    private final CustomStompHeaderValidator customStompHeaderValidator;

    protected void doFilter(StompHeaderAccessor accessor) {
        customStompHeaderValidator.validate(accessor);
        String authorization = accessor.getFirstNativeHeader(HEADER);
        if (authorization == null || !authorization.startsWith(BEARER)) {
            throw new MessagingException("Not correct JWT in auth");
        }
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

    private JWTAuthToken createToken(String jwt) {
        return new JWTAuthToken(
                jwtUtils.getUsername(jwt),
                Collections.singletonList(new SimpleGrantedAuthority(jwtUtils.getRole(jwt)))
        );
    }

}
