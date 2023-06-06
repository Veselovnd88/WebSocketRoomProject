package ru.veselov.websocketroomproject.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthProperties {

    private String header;

    private String prefix;

    private String usernameClaim;

    private String roleClaim;

    private String chatEventURL;

    private String secret;

}
