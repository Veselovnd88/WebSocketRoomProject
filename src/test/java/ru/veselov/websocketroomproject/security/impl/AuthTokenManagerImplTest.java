package ru.veselov.websocketroomproject.security.impl;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.security.AuthTokenManager;

@SpringBootTest
class AuthTokenManagerImplTest {

    @Autowired
    AuthTokenManager authTokenManager;

    @Test
    void shouldCreateAndAuthenticateToken() {

        UsernamePasswordAuthenticationToken token = authTokenManager
                .createAndAuthenticateToken(TestConstants.BEARER_JWT);

        Assertions.assertThat(token.getPrincipal()).isEqualTo("user1");
        Assertions.assertThat(token.getCredentials()).isEqualTo(TestConstants.BEARER_JWT.substring(7));
        Assertions.assertThat(token.getAuthorities()).contains(new SimpleGrantedAuthority("admin"));
    }

    @Test
    void shouldThrowException() {

        Assertions.assertThatThrownBy(
                () -> authTokenManager.createAndAuthenticateToken("Bearer ")).isInstanceOf(JWTDecodeException.class);
    }

}