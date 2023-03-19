package ru.veselov.websocketroomproject.config.interceptor;

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

import java.util.Map;

@SpringBootTest
class SocketSubscriptionInterceptorTest {
    @Value("${socket.dest-prefix}")
    private String destinationPrefix;

    @Test
    void shouldReturnMessage() {
        ChannelInterceptor interceptor = new SocketSubscriptionInterceptor(destinationPrefix);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "stompCommand", StompCommand.SUBSCRIBE,
                "simpDestination", "/topic/users/5",
                "simpSessionId", "test"
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
    }

    @Test
    void shouldReturnMessageIfAnotherCommand() {
        ChannelInterceptor interceptor = new SocketSubscriptionInterceptor(destinationPrefix);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "stompCommand", StompCommand.CONNECT,
                "simpDestination", "/topic/users/5",
                "simpSessionId", "test"
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
    }

    @Test
    void shouldThrowMessagingExceptionIfDestinationIsNull() {
        ChannelInterceptor interceptor = new SocketSubscriptionInterceptor("/topic");
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "stompCommand", StompCommand.SUBSCRIBE,
                "simpSessionId", "test"
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionIfDestinationIsNotCorrect() {
        ChannelInterceptor interceptor = new SocketSubscriptionInterceptor("/topic");
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                "stompCommand", StompCommand.SUBSCRIBE,
                "simpSessionId", "test",
                "simpDestination", "/tRopic/users/5"
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

}