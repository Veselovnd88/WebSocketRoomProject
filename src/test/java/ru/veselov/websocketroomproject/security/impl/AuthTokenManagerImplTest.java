package ru.veselov.websocketroomproject.security.impl;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.veselov.websocketroomproject.security.AuthTokenManager;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthTokenManagerImplTest {

    private static final String BEARER_JWT = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidX" +
            "Nlcm5hbWUiOiJ1c2VyMSIsInJvbGUiOiJhZG1pbiJ9.vDluIRzAjSOxbq8I4tLPUR_koUl7GPkAq34xjsuA1Ds";

    @Autowired
    AuthTokenManager authTokenManager;

    @Test
    void shouldCreateToken() {

        UsernamePasswordAuthenticationToken token = authTokenManager.createToken(BEARER_JWT);

        Assertions.assertThat(token.getPrincipal()).isEqualTo("user1");
        Assertions.assertThat(token.getCredentials()).isEqualTo(BEARER_JWT.substring(7));
        Assertions.assertThat(token.getAuthorities()).contains(new SimpleGrantedAuthority("admin"));
    }

    @Test
    void shouldThrowException() {

        Assertions.assertThatThrownBy(
                () -> authTokenManager.createToken("Bearer ")).isInstanceOf(JWTDecodeException.class);
    }

    @Test
    void shouldSetAuthentication() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("vasya", "creds",
                Collections.emptyList());

        authTokenManager.setAuthentication(token);

        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(token);
    }

}