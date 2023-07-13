package ru.veselov.websocketroomproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "task-executor")
@Data
public class ThreadTaskExecutorProperties {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

}
