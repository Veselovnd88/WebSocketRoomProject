package ru.veselov.websocketroomproject.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JWTUtils {

    public String getUsername(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaim("username").asString();
    }

    public String getRole(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaim("role").asString();
    }
}
