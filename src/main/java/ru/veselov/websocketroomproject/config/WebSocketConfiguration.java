package ru.veselov.websocketroomproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.veselov.websocketroomproject.config.interceptor.Base64DecoderInterceptor;
import ru.veselov.websocketroomproject.config.interceptor.Base64EncoderInterceptor;
import ru.veselov.websocketroomproject.config.interceptor.SocketConnectionInterceptor;
import ru.veselov.websocketroomproject.config.interceptor.SocketSubscriptionInterceptor;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Value("${socket.endpoint}")
    private String endpoint;

    @Value("${socket.dest-prefix}")
    private String destinationPrefix;

    @Value("${socket.app-prefix}")
    private String appPrefix;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(endpoint).setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint(endpoint).setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(destinationPrefix);
        registry.setApplicationDestinationPrefixes(appPrefix);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new SocketSubscriptionInterceptor(destinationPrefix),
                new SocketConnectionInterceptor(),
                new Base64DecoderInterceptor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new Base64EncoderInterceptor());
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new ByteArrayMessageConverter());
        return false;
    }
}
