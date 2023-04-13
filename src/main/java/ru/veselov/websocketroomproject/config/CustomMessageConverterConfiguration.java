package ru.veselov.websocketroomproject.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
@RequiredArgsConstructor
public class CustomMessageConverterConfiguration {
    private final Jackson2ObjectMapperBuilder builder;

    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        builder.modules(new JavaTimeModule());
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(builder.build());
        return messageConverter;
    }

}