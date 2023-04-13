package ru.veselov.websocketroomproject.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import java.text.SimpleDateFormat;

@Configuration
@RequiredArgsConstructor
public class CustomMessageConverterConfiguration {
    //private final Jackson2ObjectMapperBuilder builder;

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder objectMapperBuilder = new Jackson2ObjectMapperBuilder();
        objectMapperBuilder.modules(new JavaTimeModule());
        objectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
       // objectMapperBuilder.dateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        objectMapperBuilder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        return objectMapperBuilder;
    }

    @Bean
    public MappingJackson2MessageConverter messageConverter() {
/*        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.modules(new JavaTimeModule());*/
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapperBuilder().build());
        return messageConverter;
    }

}