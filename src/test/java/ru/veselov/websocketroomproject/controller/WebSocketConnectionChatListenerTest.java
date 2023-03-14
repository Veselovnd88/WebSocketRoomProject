package ru.veselov.websocketroomproject.controller;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import ru.veselov.websocketroomproject.cache.ChatUsersCache;
import ru.veselov.websocketroomproject.cache.SessionCache;
import ru.veselov.websocketroomproject.dto.ChatMessageDTO;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.exception.NoRoomFoundException;
import ru.veselov.websocketroomproject.exception.NoUserFoundException;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.model.RoomModel;
import ru.veselov.websocketroomproject.model.TagModel;
import ru.veselov.websocketroomproject.model.UserModel;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.UserService;

import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(username = "testUser")
class WebSocketConnectionChatListenerTest {
    @Autowired
    private WebSocketConnectionChatListener webSocketConnectionChatListener;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatUsersCache chatUsersCache;

    @Autowired
    private SessionCache sessionCache;

    private Authentication authentication;

    @MockBean
    private UserService userService;

    @MockBean
    RoomService roomService;
    Map<String, Object> headers = new HashMap<>();

    Message<byte[]> message;
    @BeforeEach
    @SuppressWarnings("unchecked")
    void init(){
        message = mock(Message.class);
        authentication = SecurityContextHolder.getContext().getAuthentication();
        headers.clear();
        chatUsersCache.clear();
        sessionCache.clear();

    }
    @Test
    @SneakyThrows
    public void connectionToTopicTest(){
        setUpUserServiceWithUser();
        setUpRoomServiceWithRoom();
        headers.put("simpDestination","/topic/users/5");
        headers.put("simpSessionId", "test");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(),message, authentication);
        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);
        verify(simpMessagingTemplate,times(1)).convertAndSend(anyString(),any(List.class));
        verify(simpMessagingTemplate,times(1)).convertAndSend(anyString(),any(ChatMessageDTO.class));
        Assertions.assertEquals(5,sessionCache.getRoom("test"));
        Assertions.assertEquals(1,chatUsersCache.getRoomUsers(5).size());
    }

    @Test
    @SneakyThrows
    public void subscriptionToIncorrectRoomNumberTest(){
        setUpUserServiceWithUser();
        headers.put("simpDestination","/topic/users/fasdfasdf");
        headers.put("simpSessionId", "test");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(),message, authentication);
        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(List.class));
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(ChatMessageDTO.class));
        Assertions.assertNull(sessionCache.getRoom("test"));
        Assertions.assertNull(chatUsersCache.getRoomUsers(5));
    }

    @Test
    @SneakyThrows
    public void subscriptionToIncorrectTopicNameTest(){
        setUpUserServiceWithUser();
        headers.put("simpDestination","/topic/us");
        headers.put("simpSessionId", "test");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(),message, authentication);
        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(List.class));
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(ChatMessageDTO.class));
        Assertions.assertNull(sessionCache.getRoom("test"));
        Assertions.assertNull(chatUsersCache.getRoomUsers(5));
    }

    @Test
    public void subscriptionNoAuthenticationTest(){
        headers.put("simpDestination","/topic/us");
        headers.put("simpSessionId", "test");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(),message, null);
        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(List.class));
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(ChatMessageDTO.class));
        Assertions.assertNull(sessionCache.getRoom("test"));
        Assertions.assertNull(chatUsersCache.getRoomUsers(5));
    }
    @Test
    @SneakyThrows
    public void subscriptionNoUserFoundTest(){
        headers.put("simpDestination","/topic/us");
        headers.put("simpSessionId", "test");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(),message, authentication);
        when(userService.findUserByUserName(anyString())).thenThrow(new NoUserFoundException());
        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(List.class));
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(ChatMessageDTO.class));
        Assertions.assertNull(sessionCache.getRoom("test"));
        Assertions.assertNull(chatUsersCache.getRoomUsers(5));
    }

    @Test
    @SneakyThrows
    public void subscriptionNoRoomFoundTest(){
        setUpUserServiceWithUser();
        headers.put("simpDestination","/topic/users/5");
        headers.put("simpSessionId", "test");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        when(message.getHeaders()).thenReturn(messageHeaders);
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(),message, authentication);
        when(roomService.findRoomById(anyInt())).thenThrow(new NoRoomFoundException());
        webSocketConnectionChatListener.handleSubscribeUser(sessionSubscribeEvent);
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(List.class));
        verify(simpMessagingTemplate,never()).convertAndSend(anyString(),any(ChatMessageDTO.class));
        Assertions.assertNull(sessionCache.getRoom("test"));
        Assertions.assertNull(chatUsersCache.getRoomUsers(5));
    }

    @Test
    public void sortingOwnerUserIsFirst(){
        for(int i=0; i<5;i++){
            ChatUser chatUser = new ChatUser();
            chatUser.setUserId(i);
            chatUser.setRoomId(5);
            chatUser.setIsOwner(false);
            chatUser.setUserName("Name "+i);
            chatUser.setSession("Session"+i);
            if(i==3){
                chatUser.setIsOwner(true);
            }
            chatUsersCache.addUser(5, chatUser);
        }
        chatUsersCache.getRoomUsers(5);
        List<ChatUserDTO> chatUserDTOS = webSocketConnectionChatListener.checkSorting(5);
        ChatUserDTO chatUserDTO = chatUserDTOS.get(0);
        Assertions.assertEquals(3,chatUserDTO.getUserId());
    }

    private void setUpUserServiceWithUser() throws NoUserFoundException {
        UserModel userModel = new UserModel();
        userModel.setId(100);
        userModel.setUsername(authentication.getName());
        when(userService.findUserByUserName(anyString())).thenReturn(userModel);
    }

    private void setUpRoomServiceWithRoom() throws NoRoomFoundException {
        RoomModel roomModel = RoomModel.builder().roomTags(
                        Set.of(new TagModel(1, "TestTag"),
                                new TagModel(2, "TestTag2"))
                )
                .roomToken("token")
                .id(5)
                .deleteTime(new Date())
                .isPublic(true)
                .name("MyRoom")
                .sourceUrl("sourceUrl")
                .owner(new UserModel(100, "Vasya", "email"))
                .build();
        when(roomService.findRoomById(anyInt())).thenReturn(roomModel);
    }

}