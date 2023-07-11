package ru.veselov.websocketroomproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "timed-task-executor")
@Data
public class TimedTaskExecutorProperties {

    int corePoolSize;

    int maxPoolSize;

    int queueCapacity;

}
