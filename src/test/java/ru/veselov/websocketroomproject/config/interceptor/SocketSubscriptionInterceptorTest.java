package ru.veselov.websocketroomproject.config.interceptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import ru.veselov.websocketroomproject.TestConstants;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class SocketSubscriptionInterceptorTest {

    private static final String DESTINATION = "/topic/users/5";

    SocketSubscriptionInterceptor interceptor;

    @BeforeEach
    void init() {
        String[] prefixes = new String[]{"/topic", "/queue"};
        String userPrefix = "/user";
        interceptor = new SocketSubscriptionInterceptor(prefixes, userPrefix);
    }


    @Test
    void shouldReturnMessage() {
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.SUBSCRIBE,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.DESTINATION_HEADER, DESTINATION
        );

        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
    }

    @Test
    void shouldReturnMessageIfAnotherCommand() {
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.CONNECT,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.DESTINATION_HEADER, DESTINATION
        );

        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
    }

    @Test
    void shouldThrowMessagingExceptionIfDestinationIsNull() {
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.SUBSCRIBE,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionIfDestinationIsNotCorrect() {
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.SUBSCRIBE,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.DESTINATION_HEADER, "/tRopic/users/5" //incorrect topic name
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

}