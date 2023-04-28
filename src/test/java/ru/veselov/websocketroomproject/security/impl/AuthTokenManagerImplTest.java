package ru.veselov.websocketroomproject.security.impl;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.security.AuthTokenManager;
import ru.veselov.websocketroomproject.security.JwtConverter;

@SpringBootTest
class AuthTokenManagerImplTest {

    @Autowired
    AuthTokenManager authTokenManager;

    @MockBean
    JwtConverter jwtConverter;

    @Captor
    ArgumentCaptor<String> substringCaptor;

    @Test
    void shouldCreateAndAuthenticateToken() {
        authTokenManager.createAndAuthenticateToken(TestConstants.BEARER_JWT);

        Mockito.verify(jwtConverter, Mockito.times(1)).convert(substringCaptor.capture());
        String value = substringCaptor.getValue();
        Assertions.assertThat(value).isEqualTo(TestConstants.BEARER_JWT.substring(7));
    }

    @ParameterizedTest
    @EmptySource
    void shouldThrowException(String jwt) {
        Assertions.assertThatThrownBy(
                () -> authTokenManager.createAndAuthenticateToken("Bearer " + jwt)).isInstanceOf(JWTDecodeException.class);
    }

}