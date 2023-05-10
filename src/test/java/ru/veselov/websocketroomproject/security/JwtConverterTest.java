package ru.veselov.websocketroomproject.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.veselov.websocketroomproject.TestConstants;

class JwtConverterTest {

    JwtConverter jwtConverter;

    @BeforeEach
    void init() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setHeader("Authorization");
        jwtProperties.setPrefix("Bearer ");
        jwtProperties.setUsernameClaim("user");
        jwtProperties.setRoleClaim("role");
        JwtUtils jwtUtils = new JwtUtils(jwtProperties);
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