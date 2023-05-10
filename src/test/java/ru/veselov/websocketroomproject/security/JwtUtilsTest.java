package ru.veselov.websocketroomproject.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.veselov.websocketroomproject.TestConstants;


class JwtUtilsTest {
    private final String JWT = TestConstants.BEARER_JWT.substring(7);

    JwtUtils jwtUtils;

    @BeforeEach
    void init() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setHeader("Authorization");
        jwtProperties.setPrefix("Bearer ");
        jwtProperties.setUsernameClaim("user");
        jwtProperties.setRoleClaim("role");
        jwtUtils = new JwtUtils(jwtProperties);
    }

    @Test
    void shouldReturnUsername() {
        String username = jwtUtils.getUsername(JWT);
        Assertions.assertThat(username).isEqualTo("user1");
    }

    @Test
    void shouldReturnRole() {
        String role = jwtUtils.getRole(JWT);
        Assertions.assertThat(role).isEqualTo("ADMIN");
    }

}