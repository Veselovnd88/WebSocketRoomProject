package ru.veselov.websocketroomproject.service.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.websocketroomproject.dto.response.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.mapper.ChatUserMapperImpl;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class EventMessageServiceImplTest {

    private static final String ROOM_ID = "5";

    private final Faker faker = new Faker();

    @InjectMocks
    EventMessageServiceImpl eventMessageService;

    @Mock
    EventSender eventSender;

    @Mock
    ChatUserService chatUserService;

    private ChatUserMapper chatUserMapper;

    @Captor
    ArgumentCaptor<EventMessageDTO<Set<ChatUserDTO>>> eventMessageCaptorSet;

    @Captor
    ArgumentCaptor<EventMessageDTO<ChatUserDTO>> eventMessageCaptorChatUser;

    @BeforeEach
    void init() {
        chatUserMapper = new ChatUserMapperImpl();
        ReflectionTestUtils.setField(
                eventMessageService,
                "chatUserMapper",
                chatUserMapper,
                ChatUserMapper.class);
    }

    @Test
    void shouldCreateEventMessageWithUsersRefreshedEventTypeAndCallSender() {
        ChatUser stubChatUser = new ChatUser(faker.name().username(), ROOM_ID, "asdf");
        Mockito.when(chatUserService.findChatUsersByRoomId(ROOM_ID)).thenReturn(Set.of(stubChatUser));

        eventMessageService.sendUserListToAllSubscriptions(ROOM_ID);

        Mockito.verify(eventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptorSet.capture());
        EventMessageDTO<Set<ChatUserDTO>> capturedMsg = eventMessageCaptorSet.getValue();
        Assertions.assertThat(capturedMsg.getEventType()).isEqualTo(EventType.USERS_REFRESHED);
        Assertions.assertThat(capturedMsg.getData()).contains(chatUserMapper.chatUserToDTO(stubChatUser));
        Mockito.verify(chatUserService, Mockito.times(1)).findChatUsersByRoomId(ROOM_ID);
    }

    @Test
    void shouldCreateEventMessageWithConnectedEventTypeAndCallSender() {
        ChatUser stubChatUser = new ChatUser(faker.name().username(), ROOM_ID, "asdf");

        eventMessageService.sendUserConnectedMessageToAll(stubChatUser);

        Mockito.verify(eventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptorChatUser.capture());
        EventMessageDTO<ChatUserDTO> capturedMsg = eventMessageCaptorChatUser.getValue();
        Assertions.assertThat(capturedMsg.getEventType()).isEqualTo(EventType.CONNECTED);
        Assertions.assertThat(capturedMsg.getData()).isEqualTo(chatUserMapper.chatUserToDTO(stubChatUser));
    }

    @Test
    void shouldCreateEventMessageWithDisconnectedEventTypeAndCallSender() {
        ChatUser stubChatUser = new ChatUser(faker.name().username(), ROOM_ID, "asdf");

        eventMessageService.sendUserDisconnectedMessageToAll(stubChatUser);

        Mockito.verify(eventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptorChatUser.capture());
        EventMessageDTO<ChatUserDTO> capturedMsg = eventMessageCaptorChatUser.getValue();
        Assertions.assertThat(capturedMsg.getEventType()).isEqualTo(EventType.DISCONNECTED);
        Assertions.assertThat(capturedMsg.getData()).isEqualTo(chatUserMapper.chatUserToDTO(stubChatUser));
    }

}