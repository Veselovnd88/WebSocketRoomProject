package ru.veselov.websocketroomproject.security.jwt.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.jwt.JwtUtil;
import ru.veselov.websocketroomproject.security.jwt.JwtValidator;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtValidatorImpl implements JwtValidator {

    private final AuthProperties authProperties;

    @Override
    public boolean isValidJwt(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey(authProperties.getSecret()))
                    .build().parseClaimsJws(jwt).getBody();
            log.info("Access token validated");
            return true;
        } catch (SignatureException |
                 MalformedJwtException |
                 UnsupportedJwtException |
                 DecodingException |
                 ExpiredJwtException |
                 IllegalArgumentException exception) {
            log.warn("Access token was not validated: {}", exception.getMessage());
            return false;
        }
    }

}
