package ru.veselov.websocketroomproject.config.interceptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.websocket.interceptor.CustomStompHeaderValidator;
import ru.veselov.websocketroomproject.websocket.interceptor.SocketConnectionInterceptor;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class SocketConnectionInterceptorTest {

    @Mock
    private CustomStompHeaderValidator customStompHeaderValidator;

    @InjectMocks
    SocketConnectionInterceptor interceptor;

    @Test
    void shouldReturnMessage() {
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.CONNECT,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        TestConstants.ROOM_ID_HEADER, List.of(TestConstants.ROOM_ID),
                        TestConstants.AUTH_HEADER, List.of("Bearer ")
                )
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
        Mockito.verify(customStompHeaderValidator, Mockito.times(1))
                .validateRoomIdHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
    }

    @Test
    void shouldReturnMessageIfAnotherCommand() {
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.SUBSCRIBE,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        TestConstants.ROOM_ID_HEADER, List.of(TestConstants.ROOM_ID),
                        TestConstants.AUTH_HEADER, List.of("asdf")
                )
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);

        Mockito.verify(customStompHeaderValidator, Mockito.never())
                .validateRoomIdHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
    }

}
