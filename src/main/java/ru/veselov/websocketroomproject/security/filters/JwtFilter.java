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
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.veselov.websocketroomproject.exception.error.ApiErrorResponse;
import ru.veselov.websocketroomproject.exception.error.ErrorCode;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.authentication.JwtAuthenticationToken;
import ru.veselov.websocketroomproject.security.jwt.JwtValidator;
import ru.veselov.websocketroomproject.security.managers.JwtAuthenticationManager;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter handle Jwt and authenticate it.
 * Additionally checked header in EventSource from FrontEnd
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtAuthenticationManager authenticationManager;

    private final AuthProperties authProperties;

    private final ObjectMapper jsonMapper;

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtOpt = getJwtFromRequest(request);
        if (jwtOpt.isEmpty()) {
            //This part is checking if ChatEventSource request has jwt header
            if (!validateChatEventSourceHeader(request, response)) {
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = jwtOpt.get();
        if (StringUtils.isNotBlank(jwt) && jwtValidator.isValidJwt(jwt)) {
            JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication for [{}] created and set to context", request.getRemoteAddr());
            }
        } else {
            sendInvalidJwtError(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendInvalidJwtError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                ErrorCode.ERROR_UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED.value(),
                "Cannot connect to [%s]: Jwt is invalid or empty".formatted(requestURI));
        String mapperMessage = jsonMapper.writeValueAsString(errorResponse);
        log.error("Cannot connect to [{}]: Jwt is invalid or empty", requestURI);
        response.getWriter().print(mapperMessage);
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader(authProperties.getHeader());
        if (StringUtils.isNotBlank(bearToken) && bearToken.startsWith(authProperties.getPrefix())) {
            return Optional.of(bearToken.substring(authProperties.getPrefix().length()));
        }
        return Optional.empty();
    }

    private boolean validateChatEventSourceHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals(authProperties.getChatEventURL())) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiErrorResponse errorResponse = new ApiErrorResponse(
                    ErrorCode.ERROR_UNAUTHORIZED,
                    HttpStatus.UNAUTHORIZED.value(),
                    "Cannot connect to [/api/v1/room/event]: Authorization header not exists or has wrong prefix");
            String mapperMessage = jsonMapper.writeValueAsString(errorResponse);
            response.getWriter().print(mapperMessage);
            log.error("Wrong authorization prefix to connect [{}], error response sent", requestURI);
            return false;
        }
        return true;
    }

}
