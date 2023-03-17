package ru.veselov.websocketroomproject.config.interceptors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class TopicSubscriptionInterceptorTest {
    @Value("${socket.dest-prefix}")
    private String destinationPrefix;

    @Test
    void shouldReturnMessage() {
        ChannelInterceptor interceptor = new TopicSubscriptionInterceptor(destinationPrefix);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("stompCommand", StompCommand.SUBSCRIBE);
        headers.put("simpDestination", "/topic/users/5");
        headers.put("simpSessionId", "test");
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
    }

    @Test
    void shouldThrowMessagingExceptionIfDestinationIsNull() {
        ChannelInterceptor interceptor = new TopicSubscriptionInterceptor("/topic");
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("stompCommand", StompCommand.SUBSCRIBE);
        headers.put("simpSessionId", "test");
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionIfDestinationIsNotCorrect() {
        ChannelInterceptor interceptor = new TopicSubscriptionInterceptor("/topic");
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("stompCommand", StompCommand.SUBSCRIBE);
        headers.put("simpSessionId", "test");
        headers.put("simpDestination", "/tRopic/users/5");
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }
}