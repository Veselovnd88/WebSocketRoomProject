package ru.veselov.websocketroomproject.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JWTUtils {

    private static final String USERNAME_CLAIM = "username";

    private static final String ROLE_CLAIM = "role";

    public String getUsername(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaim(USERNAME_CLAIM).asString();
    }

    public String getRole(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaim(ROLE_CLAIM).asString();
    }
}