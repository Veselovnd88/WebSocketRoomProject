package ru.veselov.websocketroomproject.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final AuthTokenManager authTokenManager;

    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(securityProperties.getHeader());
        if (authHeader == null || !authHeader.startsWith(securityProperties.getPrefix())) {
            String requestURI = request.getRequestURI();
            if (requestURI.equals(securityProperties.getChatEventURL())) {
                log.warn("Wrong authorization prefix to connect [{}]", requestURI);
                throw new JWTDecodeException(
                        "Cannot connect to [/api/room/event]: Authorization header not exists or has wrong prefix"
                );
            }
            filterChain.doFilter(request, response);
            return;
        }
        authTokenManager.createAndAuthenticateToken(authHeader);
        log.info("Authentication for [{}] created and set to context", request.getRemoteAddr());
        filterChain.doFilter(request, response);
    }

}