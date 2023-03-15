package ru.veselov.websocketroomproject.listener;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.exception.UserNotFoundException;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.User;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(username = "testUser")
class WebSocketConnectionChatListenerTest {
    @Autowired
    private WebSocketConnectionChatListener webSocketConnectionChatListener;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    private Authentication authentication;
    @MockBean
    private UserService userService;
    @MockBean
    RoomService roomService;
    Map<String, Object> headers = new HashMap<>();
    Message<byte[]> message;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void init() {
        message = Mockito.mock(Message.class);
        authentication = SecurityContextHolder.getContext().getAuthentication();
        headers.clear();
    }

    @Test
    public void shouldConnectToTopic() {
        User user = new User(100, authentication.getName(), "email");
        Mockito.when(userService.findUserByUserName(ArgumentMatchers.anyString())).thenReturn(user);
        Room room = new Room(
                5,
                "testRoom",
                true,
                "url",
                "token",
                new Date(),
                new User(100, "Vasya", "email"));
        Mockito.when(roomService.findRoomById(ArgumentMatchers.anyInt())).thenReturn(room);
        Map<String, Object> headers = new HashMap<>();
        headers.put("simpDestination", "/topic/users/5");
        headers.put("simpSessionId", "test");
        headers.put("roomId", "5");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }

    @Test
    public void shouldInterruptAndLogErrorRoomIdIsNotInt() {
        User user = new User(100, authentication.getName(), "email");
        Mockito.when(userService.findUserByUserName(ArgumentMatchers.anyString())).thenReturn(user);
        headers.put("simpDestination", "/topic/users/fasdfasdf");
        headers.put("simpSessionId", "test");
        headers.put("roomId", "fasdfasdf");
        Map<String, Object> headers = new HashMap<>();
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }

    @Test
    public void shouldInterruptAndLogErrorRoomIdIsNull() {
        User user = new User(100, authentication.getName(), "email");
        Mockito.when(userService.findUserByUserName(ArgumentMatchers.anyString())).thenReturn(user);
        headers.put("simpDestination", "/topic/users/5");
        headers.put("simpSessionId", "test");
        Map<String, Object> headers = new HashMap<>();
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }

    @Test
    @SneakyThrows
    public void shouldInterruptAndLogErrorWithTopicIsNull() {
        User user = new User(100, authentication.getName(), "email");
        Mockito.when(userService.findUserByUserName(ArgumentMatchers.anyString())).thenReturn(user);
        Map<String, Object> headers = new HashMap<>();
        headers.put("simpSessionId", "test");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }

    @Test
    public void subscriptionNoAuthenticationTest() {
        headers.put("simpDestination", "/topic/us");
        headers.put("simpSessionId", "test");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, null);
        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);
        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }

    @Test
    public void subscriptionNoUserFoundTest() {
        headers.put("simpDestination", "/topic/users/5");
        headers.put("simpSessionId", "test");
        headers.put("roomId", "5");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);
        Mockito.when(userService.findUserByUserName(ArgumentMatchers.anyString())).thenThrow(new UserNotFoundException());
        Assertions.assertThrows(UserNotFoundException.class, () -> webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent));
        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }

    @Test
    public void subscriptionNoRoomFoundTest() {
        headers.put("simpDestination", "/topic/users/5");
        headers.put("simpSessionId", "test");
        headers.put("roomId", "5");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);
        Mockito.when(roomService.findRoomById(ArgumentMatchers.anyInt())).thenThrow(new RoomNotFoundException());
        Assertions.assertThrows(RoomNotFoundException.class, () -> webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent));
        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }
}