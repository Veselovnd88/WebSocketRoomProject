package ru.veselov.websocketroomproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "socket")
@Data
public class WebSocketProperties {
    private String[] destPrefixes;
    private String userPrefix;
    private String appPrefix;
    private String endpoint;
    private String chatTopic;
    private String youtubeTopic;
    private String privateMessageTopic;
    private String headerRoomId;
    private int messageSizeLimit;
    private int sendTimeLimit;
    private int bufferSizeLimit;

}
