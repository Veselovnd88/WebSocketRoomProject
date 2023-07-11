package ru.veselov.websocketroomproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class TimedThreadPoolTaskExecutorConfiguration {

    private final TimedTaskExecutorProperties timedTaskExecutorProperties;

    @Bean
    @Qualifier("timedTaskExecutor")
    public ThreadPoolTaskExecutor timedTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(timedTaskExecutorProperties.getCorePoolSize());
        taskExecutor.setMaxPoolSize(timedTaskExecutorProperties.getMaxPoolSize());
        taskExecutor.setQueueCapacity(timedTaskExecutorProperties.getQueueCapacity());
        return taskExecutor;
    }
}
