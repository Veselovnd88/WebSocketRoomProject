package ru.veselov.websocketroomproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.veselov.websocketroomproject.config.resolver.SortParameterRequestParamsResolver;

import java.util.List;

/**
 * Configuration class for using Flux reactive response
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${chat-event.core-pool-size}")
    private int corePoolSize;

    @Value("${chat-event.max-pool-size}")
    private int maxPoolSize;

    @Value("${chat-event.max-pool-size}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor mvcTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        return taskExecutor;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(mvcTaskExecutor());
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(new SortParameterRequestParamsResolver(true));
    }

}
