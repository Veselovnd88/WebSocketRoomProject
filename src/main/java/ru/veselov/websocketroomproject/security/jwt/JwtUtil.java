package ru.veselov.websocketroomproject.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtUtil {

    public static Key getKey(String secret) {
        //converting key from application.yml
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private JwtUtil() {
    }

}
