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
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.veselov.websocketroomproject.TestConstants;

import java.util.List;
import java.util.Map;

@SpringBootTest
@WithMockUser(username = "testUser")
class SocketConnectionInterceptorTest {

    private static final String ROOM_ID = "5";

    private static final String AUTH_HEADER = "Authorization";

    @Value("${socket.header-room-id}")
    private String roomIdHeader;

    @Test
    void shouldReturnMessage() {
        SocketConnectionInterceptor interceptor = new SocketConnectionInterceptor();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.CONNECT,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.USER_HEADER, authentication,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        roomIdHeader, List.of(ROOM_ID),
                        AUTH_HEADER, "asdf"
                )
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
    }

    @Test
    void shouldReturnMessageIfAnotherCommand() {
        SocketConnectionInterceptor interceptor = new SocketConnectionInterceptor();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.SUBSCRIBE,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.USER_HEADER, authentication,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(roomIdHeader, List.of(ROOM_ID))
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
    }

    @Test
    void shouldThrowMessagingExceptionWithNoAuthenticatedUser() {
        SocketConnectionInterceptor interceptor = new SocketConnectionInterceptor();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.CONNECT,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(roomIdHeader, List.of(ROOM_ID))
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionWithRoomIdIsNull() {
        SocketConnectionInterceptor interceptor = new SocketConnectionInterceptor();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.CONNECT,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.USER_HEADER, authentication
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionWithRoomIdIsEmpty() {
        SocketConnectionInterceptor interceptor = new SocketConnectionInterceptor();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.CONNECT,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.USER_HEADER, authentication,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(roomIdHeader, List.of(""))  //empty
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);
    }

}