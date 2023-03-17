package ru.veselov.websocketroomproject.config.interceptors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@WithMockUser(username = "testUser")
class SocketConnectionInterceptorTest {
    @Test
    void shouldReturnMessage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ChannelInterceptor interceptor = new SocketConnectionInterceptor();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("stompCommand", StompCommand.CONNECT);
        headers.put("simpSessionId", "test");
        headers.put("simpUser", authentication);
        headers.put("nativeHeaders", Map.of("roomId", List.of("5")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
    }

    @Test
    void shouldThrowMessagingExceptionWithNoAuthenticatedUser() {
        ChannelInterceptor interceptor = new SocketConnectionInterceptor();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("stompCommand", StompCommand.CONNECT);
        headers.put("simpSessionId", "test");
        headers.put("nativeHeaders", Map.of("roomId", List.of("5")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionWithRoomIdIsNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ChannelInterceptor interceptor = new SocketConnectionInterceptor();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("stompCommand", StompCommand.CONNECT);
        headers.put("simpSessionId", "test");
        headers.put("simpUser", authentication);
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionWithRoomIdIsEmpty() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ChannelInterceptor interceptor = new SocketConnectionInterceptor();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("stompCommand", StompCommand.CONNECT);
        headers.put("simpSessionId", "test");
        headers.put("simpUser", authentication);
        headers.put("nativeHeaders", Map.of("roomId", List.of("")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionWithRoomIdIsNotInt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ChannelInterceptor interceptor = new SocketConnectionInterceptor();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("stompCommand", StompCommand.CONNECT);
        headers.put("simpSessionId", "test");
        headers.put("simpUser", authentication);
        headers.put("nativeHeaders", Map.of("roomId", List.of("abc")));
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }
}