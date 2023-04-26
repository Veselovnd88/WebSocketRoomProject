package ru.veselov.websocketroomproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.*;
import ru.veselov.websocketroomproject.config.interceptor.CustomStompHeaderValidator;
import ru.veselov.websocketroomproject.config.interceptor.SocketConnectionInterceptor;
import ru.veselov.websocketroomproject.config.interceptor.SocketMessageInterceptor;
import ru.veselov.websocketroomproject.config.interceptor.SocketSubscriptionInterceptor;
import ru.veselov.websocketroomproject.security.JWTUtils;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final MappingJackson2MessageConverter jackson2MessageConverter;

    private final WebSocketProperties webSocketProperties;

    private final JWTUtils jwtUtils;
    private final CustomStompHeaderValidator customStompHeaderValidator;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(webSocketProperties.getEndpoint()).setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint(webSocketProperties.getEndpoint()).setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(webSocketProperties.getDestPrefixes());
        registry.setApplicationDestinationPrefixes(webSocketProperties.getAppPrefix());
        registry.setUserDestinationPrefix(webSocketProperties.getUserPrefix());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new SocketSubscriptionInterceptor(
                        webSocketProperties.getDestPrefixes(),
                        webSocketProperties.getUserPrefix()
                ),
                new SocketConnectionInterceptor(customStompHeaderValidator),
                new SocketMessageInterceptor(jwtUtils,customStompHeaderValidator)
        );
    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(webSocketProperties.getMessageSizeLimit());
        registry.setSendBufferSizeLimit(webSocketProperties.getBufferSizeLimit());
        registry.setSendTimeLimit(webSocketProperties.getSendTimeLimit());
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(jackson2MessageConverter);
        return false;
    }

}