package ru.veselov.websocketroomproject.security.jwt.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.security.AuthProperties;

@ExtendWith(MockitoExtension.class)
class JwtValidatorImplTest {

    JwtValidatorImpl jwtValidator;

    @BeforeEach
    void init() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setSecret(TestConstants.SECRET);
        jwtValidator = new JwtValidatorImpl(authProperties);
    }

    @Test
    void shouldValidateToken() {
        Assertions.assertThat(
                jwtValidator.isValidJwt(TestConstants.BEARER_JWT.substring(7))
        ).isTrue();
    }

    @Test
    void shouldNotValidateToken() {
        Assertions.assertThat(
                jwtValidator.isValidJwt(TestConstants.BEARER_JWT.substring(8))
        ).isFalse();
    }

}
