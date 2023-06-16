package ru.veselov.websocketroomproject.config.interceptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.security.AuthProperties;
import ru.veselov.websocketroomproject.security.authentication.JwtAuthenticationToken;
import ru.veselov.websocketroomproject.security.jwt.JwtValidator;
import ru.veselov.websocketroomproject.security.managers.JwtAuthenticationManager;
import ru.veselov.websocketroomproject.websocket.interceptor.CustomStompHeaderValidator;
import ru.veselov.websocketroomproject.websocket.interceptor.SocketMessageInterceptor;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class SocketMessageInterceptorTest {
    //FIXME change to JwtInterceptor
    @Mock
    private CustomStompHeaderValidator customStompHeaderValidator;

    @Mock
    JwtAuthenticationManager authManager;

    @Mock
    JwtValidator jwtValidator;

    SocketMessageInterceptor interceptor;


    @BeforeEach
    void init() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setHeader("Authorization");
        authProperties.setPrefix("Bearer ");
        authProperties.setSecret(TestConstants.SECRET);
        interceptor = new SocketMessageInterceptor(authProperties,
                customStompHeaderValidator,
                authManager,
                jwtValidator);
    }

    @Test
    void shouldReturnMessageWithCorrectedHeader() {
        //given
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken("String");
        jwtAuthenticationToken.setAuthenticated(true);
        Mockito.when(jwtValidator.isValidJwt(ArgumentMatchers.anyString())).thenReturn(true);
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
        JwtAuthenticationToken token = new JwtAuthenticationToken("user1");
        token.setAuthenticated(true);
        Mockito.when(authManager.authenticate(ArgumentMatchers.any())).thenReturn(token);

        //when
        Message<?> processedMessage = interceptor.preSend(message, channel);

        //then
        Mockito.verify(authManager, Mockito.times(1)).authenticate(ArgumentMatchers.any());
        Mockito.verify(customStompHeaderValidator, Mockito.times(1))
                .validateAuthHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
        Assertions.assertThat(processedMessage).isInstanceOf(Message.class);
        //checking if token goes to stompHeader
        Assertions.assertThat(processedMessage.getHeaders())
                .containsEntry(SimpMessageHeaderAccessor.USER_HEADER, token);
    }

    @Test
    void shouldReturnMessageIfAnotherCommand() {
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
        Mockito.verify(authManager, Mockito.never()).authenticate(ArgumentMatchers.any());
        Mockito.verify(customStompHeaderValidator, Mockito.never())
                .validateAuthHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
    }

    @Test
    void shouldThrowExceptionIfBadJwtInHeader() {
        //given
        Mockito.when(jwtValidator.isValidJwt(ArgumentMatchers.anyString())).thenReturn(false);
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

        //when
        Assertions.assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(MessagingException.class);

        //then
        Mockito.verify(authManager, Mockito.times(0)).authenticate(ArgumentMatchers.any());
        Mockito.verify(customStompHeaderValidator, Mockito.times(1))
                .validateAuthHeader(ArgumentMatchers.any(StompHeaderAccessor.class));
    }

}
