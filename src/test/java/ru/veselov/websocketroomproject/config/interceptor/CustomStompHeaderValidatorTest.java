package ru.veselov.websocketroomproject.config.interceptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.security.AuthProperties;

@ExtendWith(MockitoExtension.class)
class CustomStompHeaderValidatorTest {

    private static final String ROOM_ID = "4";

    CustomStompHeaderValidator customStompHeaderValidator;

    @BeforeEach
    void init() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setHeader("Authorization");
        authProperties.setPrefix("Bearer ");
        customStompHeaderValidator = new CustomStompHeaderValidator(authProperties);
    }

    @Test
    void shouldValidateWithoutExceptions() {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(StompCommand.MESSAGE);
        stompHeaderAccessor.addNativeHeader(TestConstants.ROOM_ID_HEADER, ROOM_ID);
        stompHeaderAccessor.addNativeHeader(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT);

        customStompHeaderValidator.validateAuthHeader(stompHeaderAccessor);
        customStompHeaderValidator.validateRoomIdHeader(stompHeaderAccessor);

    }

    @Test
    void shouldThrowMessagingExceptionIfNoAuthHeader() {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(StompCommand.MESSAGE);

        Assertions.assertThatThrownBy(
                () -> customStompHeaderValidator.validateAuthHeader(stompHeaderAccessor)
        ).isInstanceOf(MessagingException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Pearer", "Shveller"})
    void shouldThrowMessagingExceptionIfNoValidHeader(String roomId) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(StompCommand.MESSAGE);
        stompHeaderAccessor.addNativeHeader(TestConstants.AUTH_HEADER, roomId);

        Assertions.assertThatThrownBy(
                () -> customStompHeaderValidator.validateAuthHeader(stompHeaderAccessor)
        ).isInstanceOf(MessagingException.class);
    }

    @Test
    void shouldThrowMessagingExceptionIfNoRoomHeader() {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(StompCommand.MESSAGE);

        Assertions.assertThatThrownBy(
                () -> customStompHeaderValidator.validateRoomIdHeader(stompHeaderAccessor)
        ).isInstanceOf(MessagingException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowMessagingExceptionIfNoValidRoomId(String roomId) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(StompCommand.MESSAGE);
        stompHeaderAccessor.addNativeHeader(TestConstants.ROOM_ID_HEADER, roomId);

        Assertions.assertThatThrownBy(
                () -> customStompHeaderValidator.validateRoomIdHeader(stompHeaderAccessor)
        ).isInstanceOf(MessagingException.class);
    }

}