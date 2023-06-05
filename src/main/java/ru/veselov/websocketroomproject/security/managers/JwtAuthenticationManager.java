package ru.veselov.websocketroomproject.security.managers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.security.providers.JwtAuthenticationProvider;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationManager implements AuthenticationManager {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (jwtAuthenticationProvider.supports(authentication.getClass())) {
            return jwtAuthenticationProvider.authenticate(authentication);
        }
        throw new BadCredentialsException("Invalid Jwt token");
    }
}
