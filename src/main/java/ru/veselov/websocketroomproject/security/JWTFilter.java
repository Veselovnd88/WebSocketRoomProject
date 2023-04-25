package ru.veselov.websocketroomproject.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private static final String HEADER = "Authorization";

    private final JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);
        if (jwt.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty JWT in Bearer header");
            log.warn("Empty jwt in bearer header");
            throw new JWTDecodeException("Empty JWT");
        } else {
            JWTAuthToken token = createToken(jwt);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(token);
            }
            log.info("Authentication created and set to context");
            filterChain.doFilter(request, response);
        }
    }

    private JWTAuthToken createToken(String jwt) {
        return new JWTAuthToken(
                jwtUtils.getUsername(jwt),
                Collections.singletonList(new SimpleGrantedAuthority(jwtUtils.getRole(jwt)))
        );
    }

}