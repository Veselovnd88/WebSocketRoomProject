package ru.veselov.websocketroomproject.config.interceptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.security.AuthTokenManager;
import ru.veselov.websocketroomproject.security.JWTProperties;
import ru.veselov.websocketroomproject.security.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;

@SpringBootTest
class SocketMessageInterceptorTest {

    @MockBean
    private CustomStompHeaderValidator customStompHeaderValidator;

    @MockBean
    AuthTokenManager authTokenManager;

    @Autowired
    JWTProperties jwtProperties;

    @Test
    void shouldReturnMessageWithCorrectedHeader() {
        SocketMessageInterceptor interceptor =
                new SocketMessageInterceptor(jwtProperties, customStompHeaderValidator, authTokenManager);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<Object> message = Mockito.spy(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.SEND,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        TestConstants.AUTH_HEADER, List.of(TestConstants.BEARER_JWT)
                )
        );
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        Mockito.when(message.getPayload()).thenReturn(new Object());
        JwtAuthenticationToken token = new JwtAuthenticationToken("user1", null, null);
        Mockito.when(authTokenManager.createAndAuthenticateToken(ArgumentMatchers.anyString())).thenReturn(token);

        Message<?> processedMessage = interceptor.preSend(message, channel);

        Mockito.verify(authTokenManager, Mockito.times(1)).createAndAuthenticateToken(ArgumentMatchers.anyString());
        Mockito.verify(customStompHeaderValidator, Mockito.times(1))
                .validateAuthHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
        Assertions.assertThat(processedMessage).isInstanceOf(Message.class);
        Assertions.assertThat(processedMessage.getHeaders().get(SimpMessageHeaderAccessor.USER_HEADER)).isEqualTo(token);
    }

    @Test
    void shouldReturnMessageIfAnotherCommand() {
        SocketMessageInterceptor interceptor =
                new SocketMessageInterceptor(jwtProperties, customStompHeaderValidator, authTokenManager);
        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                TestConstants.COMMAND_HEADER, StompCommand.SUBSCRIBE,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(
                        TestConstants.AUTH_HEADER, List.of("asdf")
                )
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));

        Assertions.assertThat(interceptor.preSend(message, channel)).isNotNull().isInstanceOf(Message.class);
        Mockito.verify(authTokenManager, Mockito.never()).createAndAuthenticateToken(ArgumentMatchers.anyString());
        Mockito.verify(customStompHeaderValidator, Mockito.never())
                .validateAuthHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
    }

}