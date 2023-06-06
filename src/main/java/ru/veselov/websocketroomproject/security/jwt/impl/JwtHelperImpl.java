package ru.veselov.websocketroomproject.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.jwt.JwtHelper;
import ru.veselov.websocketroomproject.security.jwt.JwtUtil;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHelperImpl implements JwtHelper {

    private final AuthProperties authProperties;

    @Override
    public String getUsername(String jwt) {
        return getBody(jwt).getSubject();
    }

    @Override
    public String getRole(String jwt) {
        return (String) getBody(jwt).get(authProperties.getRoleClaim());
    }


    private Claims getBody(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(JwtUtil.getKey(authProperties.getSecret()))
                .build().parseClaimsJws(jwt).getBody();
    }

}
