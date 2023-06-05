package ru.veselov.websocketroomproject.security.providers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.security.authentication.JwtAuthenticationToken;
import ru.veselov.websocketroomproject.security.jwt.JwtParser;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtParser jwtParser;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
        String jwt = authToken.getJwt();
        String username = jwtParser.getUsername(jwt);
        String role = jwtParser.getRole(jwt);
        JwtAuthenticationToken token = new JwtAuthenticationToken(
                List.of(new SimpleGrantedAuthority(role)),
                username,
                true,
                jwt);
        token.setDetails(authToken.getDetails());
        log.info("Created jwtAuthentication token for [user {}]", username);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }
}
