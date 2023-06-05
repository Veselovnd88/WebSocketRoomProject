package ru.veselov.websocketroomproject.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.veselov.websocketroomproject.exception.error.ErrorConstants;
import ru.veselov.websocketroomproject.exception.error.ErrorResponse;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.authentication.JwtAuthenticationToken;
import ru.veselov.websocketroomproject.security.managers.JwtAuthenticationManager;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtAuthenticationManager authenticationManager;

    private final AuthProperties authProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtOpt = getJwtFromRequest(request);
        if (jwtOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = jwtOpt.get();
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        Authentication authentication = authenticationManager.authenticate(token);
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(token);
            log.info("Authentication for [{}] created and set to context", request.getRemoteAddr());
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader(authProperties.getHeader());
        if (StringUtils.isNotBlank(bearToken) && bearToken.startsWith(authProperties.getPrefix())) {
            return Optional.of(bearToken.substring(authProperties.getPrefix().length()));
        }
        return Optional.empty();
    }

    private void sendWrongChatEventPathResponse(HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = new ErrorResponse(ErrorConstants.ERROR_NOT_AUTHORIZED, "Invalid chat event path");
        String errorMessage = objectMapper.writeValueAsString(errorResponse);
        response.setContentType("application/json");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().println(errorMessage);
    }

}