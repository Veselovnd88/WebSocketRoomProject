package ru.veselov.websocketroomproject.security.jwt.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.security.AuthProperties;

@ExtendWith(MockitoExtension.class)
class JwtHelperImplTest {

    JwtHelperImpl jwtHelper;

    @BeforeEach
    void init() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setSecret(TestConstants.SECRET);
        authProperties.setRoleClaim("role");
        jwtHelper = new JwtHelperImpl(authProperties);
    }

    @Test
    void shouldReturnUsernameFromSubject() {
        String username = jwtHelper.getUsername(TestConstants.BEARER_JWT.substring(7));

        Assertions.assertThat(username).isEqualTo("user1");
    }

    @Test
    void shouldReturnRole() {
        String role = jwtHelper.getRole(TestConstants.BEARER_JWT.substring(7));

        Assertions.assertThat(role).isEqualTo("ADMIN");
    }

}
