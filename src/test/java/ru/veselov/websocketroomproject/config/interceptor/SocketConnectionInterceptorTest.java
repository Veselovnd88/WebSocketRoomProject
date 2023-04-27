package ru.veselov.websocketroomproject.config.interceptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import ru.veselov.websocketroomproject.TestConstants;

import java.util.List;
import java.util.Map;

@SpringBootTest
class SocketConnectionInterceptorTest {

    private static final String ROOM_ID = "5";

    @Value("${socket.header-room-id}")
    private String roomIdHeader;

    @MockBean
    private CustomStompHeaderValidator customStompHeaderValidator;

    @Test
    void shouldReturnMessage() {
        SocketConnectionInterceptor interceptor = new SocketConnectionInterceptor(customStompHeaderValidator);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.CONNECT,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        roomIdHeader, List.of(ROOM_ID),
                        TestConstants.AUTH_HEADER, List.of("Bearer ")
                )
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
        Mockito.verify(customStompHeaderValidator, Mockito.times(1))
                .validateRoomIdHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
        Mockito.verify(customStompHeaderValidator, Mockito.times(1))
                .validateAuthHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
    }

    @Test
    void shouldReturnMessageIfAnotherCommand() {
        SocketConnectionInterceptor interceptor = new SocketConnectionInterceptor(customStompHeaderValidator);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.SUBSCRIBE,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        roomIdHeader, List.of(ROOM_ID),
                        TestConstants.AUTH_HEADER, List.of("asdf")
                )
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);

        Mockito.verify(customStompHeaderValidator, Mockito.never())
                .validateRoomIdHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
        Mockito.verify(customStompHeaderValidator, Mockito.never())
                .validateAuthHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
    }

}