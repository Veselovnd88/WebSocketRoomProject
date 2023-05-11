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
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.setHeader("Authorization");
        securityProperties.setPrefix("Bearer ");
        securityProperties.setUsernameClaim("user");
        securityProperties.setRoleClaim("role");
        jwtUtils = new JwtUtils(securityProperties);
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