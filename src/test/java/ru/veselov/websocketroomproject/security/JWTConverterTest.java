package ru.veselov.websocketroomproject.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.veselov.websocketroomproject.TestConstants;

@SpringBootTest
class JWTConverterTest {

    @Autowired
    JWTConverter jwtConverter;

    @Test
    void shouldConvertJwtToToken() {
        JwtAuthenticationToken token = jwtConverter.convert(TestConstants.BEARER_JWT.substring(7));

        Assertions.assertThat(token.getPrincipal()).isEqualTo("user1");
        Assertions.assertThat(token.getCredentials()).isEqualTo(TestConstants.BEARER_JWT.substring(7));
        Assertions.assertThat(token.getAuthorities().contains(new SimpleGrantedAuthority("admin"))).isTrue();
    }

}