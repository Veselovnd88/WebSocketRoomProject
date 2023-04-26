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
public class JWTFilter extends OncePerRequestFilter {

    private final AuthTokenManager authTokenManager;

    private final JWTProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(jwtProperties.getHeader());
        if (authHeader == null || !authHeader.startsWith(jwtProperties.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);
        if (jwt.isBlank()) {
            log.warn("Empty jwt in bearer header");
            throw new JWTDecodeException("Empty JWT in Bearer header");
        } else {
            JWTAuthToken token = authTokenManager.createToken(jwt);
            authTokenManager.setAuthentication(token);
            log.info("Authentication created and set to context");
            filterChain.doFilter(request, response);
        }
    }

}