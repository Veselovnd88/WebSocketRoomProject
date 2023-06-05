package ru.veselov.websocketroomproject.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {

    private final AuthProperties authProperties;

    public String getUsername(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getSubject();
    }

    public String getRole(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaim(authProperties.getRoleClaim()).asString();
    }

}