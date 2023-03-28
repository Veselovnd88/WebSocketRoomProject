package ru.veselov.websocketroomproject.listener;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.SendMessageDTO;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SpringBootTest
@WithMockUser(username = "testUser")
class WebSocketSubscriptionListenerTest {

    private static final String ROOM_ID = "5";

    private static final String DESTINATION = "/topic/users/5";

    @Value("${socket.header-room-id}")
    private String roomIdHeader;

    @Autowired
    private WebSocketSubscriptionListener webSocketSubscriptionListener;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    @MockBean
    private ChatUserService chatUserService;

    private final Faker faker = new Faker();

    @Captor
    ArgumentCaptor<SendMessageDTO<List<ChatUserDTO>>> argumentCaptor;

    @Test
    void shouldConnectToChosenTopic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                StompHeaderAccessor.DESTINATION_HEADER, DESTINATION,
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(roomIdHeader, List.of(ROOM_ID))
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);
        //Creating set with fake users
        Mockito.when(chatUserService.findChatUsersByRoomId(ROOM_ID)).thenReturn(
                new HashSet<>(
                        faker.collection(
                                () -> new ChatUser(faker.name().username(),
                                        ROOM_ID,
                                        faker.expression("#{letterify '???????'}")) //random chars
                        ).maxLen(4).generate())
        );

        webSocketSubscriptionListener.handleUserSubscription(sessionSubscribeEvent);

        Mockito.verify(chatUserService, Mockito.times(1)).findChatUsersByRoomId(ROOM_ID);
        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSend(ArgumentMatchers.anyString(), argumentCaptor.capture());
        Assertions.assertThat(argumentCaptor.getValue()).isInstanceOf(SendMessageDTO.class).isNotNull();
        Assertions.assertThat(argumentCaptor.getValue().getMessage()).hasSize(4)
                .hasAtLeastOneElementOfType(ChatUserDTO.class);
    }

    @Test
    void shouldNotConnectToNotChosenTopic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Message<byte[]> message = Mockito.mock(Message.class);
        Map<String, Object> headers = Map.of(
                StompHeaderAccessor.DESTINATION_HEADER, "/topic/notUsers/5",    //not correct topic
                StompHeaderAccessor.SESSION_ID_HEADER, TestConstants.TEST_SESSION_ID,
                StompHeaderAccessor.NATIVE_HEADERS, Map.of(roomIdHeader, List.of(ROOM_ID))
        );
        Mockito.when(message.getHeaders()).thenReturn(new MessageHeaders(headers));
        SessionSubscribeEvent sessionSubscribeEvent = new SessionSubscribeEvent(new Object(), message, authentication);

        webSocketSubscriptionListener.handleUserSubscription(sessionSubscribeEvent);

        Mockito.verify(simpMessagingTemplate, Mockito.never())
                .convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class));
    }

}