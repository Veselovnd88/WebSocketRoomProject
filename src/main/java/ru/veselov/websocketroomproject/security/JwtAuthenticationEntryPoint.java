package ru.veselov.websocketroomproject.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.exception.error.ApiErrorResponse;
import ru.veselov.websocketroomproject.exception.error.ErrorCode;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ApiErrorResponse errorMessage = new ApiErrorResponse(
                ErrorCode.ERROR_UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED.value(),
                "Something went wrong with authentication");
        String mappedMessage = objectMapper.writeValueAsString(errorMessage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().println(mappedMessage);
        log.error("Error occurred during authentication through jwt");
    }
}
