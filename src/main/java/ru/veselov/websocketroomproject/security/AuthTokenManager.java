package ru.veselov.websocketroomproject.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthTokenManager {

    private final JWTUtils jwtUtils;

    public JWTAuthToken createToken(String authHeader) {
        String jwt = authHeader.substring(7);
        return new JWTAuthToken(
                jwtUtils.getUsername(jwt),
                Collections.singletonList(new SimpleGrantedAuthority(jwtUtils.getRole(jwt)))
        );
    }

    public void setAuthentication(JWTAuthToken jwtAuthToken) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);
        }
    }


}
