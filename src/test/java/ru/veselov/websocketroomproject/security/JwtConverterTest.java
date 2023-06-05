package ru.veselov.websocketroomproject.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.security.authentication.JwtAuthenticationToken;

class JwtConverterTest {

    JwtConverter jwtConverter;

    @BeforeEach
    void init() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setHeader("Authorization");
        authProperties.setPrefix("Bearer ");
        authProperties.setUsernameClaim("user");
        authProperties.setRoleClaim("role");
        JwtUtils jwtUtils = new JwtUtils(authProperties);
        jwtConverter = new JwtConverter(jwtUtils);
    }

    @Test
    void shouldConvertJwtToToken() {
        JwtAuthenticationToken token = jwtConverter.convert(TestConstants.BEARER_JWT.substring(7));

        Assertions.assertThat(token.getPrincipal()).isEqualTo("user1");
        Assertions.assertThat(token.getCredentials()).isEqualTo(TestConstants.BEARER_JWT.substring(7));
        Assertions.assertThat(token.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))).isTrue();
    }

}