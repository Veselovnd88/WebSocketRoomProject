package ru.veselov.websocketroomproject.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTConverter implements Converter<String, UsernamePasswordAuthenticationToken> {

    private final JWTUtils jwtUtils;

    @Override
    public UsernamePasswordAuthenticationToken convert(@NonNull String jwt) {
        String username = jwtUtils.getUsername(jwt);
        String role = jwtUtils.getRole(jwt);
        return new UsernamePasswordAuthenticationToken(username,
                jwt,
                Collections.singletonList(new SimpleGrantedAuthority(role)));
    }

}