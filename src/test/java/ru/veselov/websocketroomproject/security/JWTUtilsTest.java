package ru.veselov.websocketroomproject.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.veselov.websocketroomproject.TestConstants;

@SpringBootTest
class JWTUtilsTest {
    private final String JWT = TestConstants.BEARER_JWT.substring(7);
    @Autowired
    JWTUtils jwtUtils;

    @Test
    void shouldReturnUsername() {
        String username = jwtUtils.getUsername(JWT);
        Assertions.assertThat(username).isEqualTo("user1");
    }

    @Test
    void shouldReturnRole() {
        String role = jwtUtils.getRole(JWT);
        Assertions.assertThat(role).isEqualTo("admin");
    }

}