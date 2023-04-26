package ru.veselov.websocketroomproject.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JWTProperties {

    private String header;

    private String prefix;

    private String usernameClaim;

    private String roleClaim;

}
