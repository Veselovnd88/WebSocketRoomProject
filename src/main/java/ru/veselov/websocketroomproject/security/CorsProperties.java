package ru.veselov.websocketroomproject.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "cors")
@Data
public class CorsProperties {

    private List<String> allowedOrigins;

    private List<String> allowedMethods;

    private List<String> allowedHeaders;

    private boolean allowCredentials;

}
