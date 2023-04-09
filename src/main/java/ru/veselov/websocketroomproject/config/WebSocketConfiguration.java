package ru.veselov.websocketroomproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import ru.veselov.websocketroomproject.config.interceptor.SocketConnectionInterceptor;
import ru.veselov.websocketroomproject.config.interceptor.SocketSubscriptionInterceptor;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final MappingJackson2MessageConverter jackson2MessageConverter;

    private final SocketConnectionInterceptor socketConnectionInterceptor;

    private final SocketSubscriptionInterceptor socketSubscriptionInterceptor;

    @Value("${socket.endpoint}")
    private String endpoint;

    @Value("${socket.dest-prefix}")
    private String destinationPrefix;

    @Value("${socket.app-prefix}")
    private String appPrefix;

    @Value("${socket.message-size-limit}")
    private Integer messageSizeLimit;

    @Value("${socket.send-time-limit}")
    private Integer sendTimeLimit;

    @Value("${socket.buffer-size-limit}")
    private Integer bufferSizeLimit;

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
                socketSubscriptionInterceptor,
                socketConnectionInterceptor
        );
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(messageSizeLimit);
        registry.setSendBufferSizeLimit(bufferSizeLimit);
        registry.setSendTimeLimit(sendTimeLimit);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(jackson2MessageConverter);
        messageConverters.add(new ByteArrayMessageConverter());
        return false;
    }
}