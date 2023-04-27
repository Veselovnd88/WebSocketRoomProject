package ru.veselov.websocketroomproject.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JWTUtilsTest {

    private static final String JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidX" +
            "Nlcm5hbWUiOiJ1c2VyMSIsInJvbGUiOiJhZG1pbiJ9.vDluIRzAjSOxbq8I4tLPUR_koUl7GPkAq34xjsuA1Ds";

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