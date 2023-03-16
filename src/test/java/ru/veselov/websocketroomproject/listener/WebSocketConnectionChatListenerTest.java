package ru.veselov.websocketroomproject.listener;

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
import java.util.List;
import java.util.Map;

@SpringBootTest
@WithMockUser(username = "testUser")
class WebSocketConnectionChatListenerTest {
    @Autowired
    private WebSocketConnectionChatListener webSocketConnectionChatListener;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    @MockBean
    private UserService userService;
    @MockBean
    RoomService roomService;

    @Test
    void shouldConnectToChosenTopic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
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
        Map<String, Object> nativeHeaders = new HashMap<>();
        nativeHeaders.put("roomId", List.of("5"));
        headers.put("nativeHeaders", nativeHeaders);
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketConnectionChatListener.handleUserSubscription(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class));
    }

    @Test
    void shouldNotConnectToNotChosenTopic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
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
        headers.put("simpDestination", "/topic/notUsers/5");
        headers.put("simpSessionId", "test");
        Map<String, Object> nativeHeaders = new HashMap<>();
        nativeHeaders.put("roomId", List.of("5"));
        headers.put("nativeHeaders", nativeHeaders);
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketConnectionChatListener.handleUserSubscription(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class));
    }

    @Test
    void shouldBeThrownUserNotFoundException() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("simpDestination", "/topic/users/5");
        headers.put("simpSessionId", "test");
        Map<String, Object> nativeHeaders = new HashMap<>();
        nativeHeaders.put("roomId", List.of("5"));
        headers.put("nativeHeaders", nativeHeaders);
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);
        Mockito.when(userService.findUserByUserName(ArgumentMatchers.anyString())).thenThrow(new UserNotFoundException());

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> webSocketConnectionChatListener.handleUserSubscription(sessionSubscribeEvent))
                .isInstanceOf(UserNotFoundException.class);
        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }

    @Test
    void shouldBeThrownRoomNotFoundException() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("simpDestination", "/topic/users/5");
        headers.put("simpSessionId", "test");
        Map<String, Object> nativeHeaders = new HashMap<>();
        nativeHeaders.put("roomId", List.of("5"));
        headers.put("nativeHeaders", nativeHeaders);
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Mockito.when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);
        Mockito.when(roomService.findRoomById(ArgumentMatchers.anyInt())).thenThrow(new RoomNotFoundException());

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> webSocketConnectionChatListener.handleUserSubscription(sessionSubscribeEvent))
                .isInstanceOf(RoomNotFoundException.class);
        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChatUserDTO.class));
    }
}