package ru.veselov.websocketroomproject.security.jwt.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.jwt.JwtParser;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtParserImpl implements JwtParser {

    private final AuthProperties authProperties;

    @Override
    public String getUsername(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getSubject();
    }

    @Override
    public String getRole(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaim(authProperties.getRoleClaim()).asString();
    }
}
