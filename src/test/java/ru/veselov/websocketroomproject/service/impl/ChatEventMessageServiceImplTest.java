package ru.veselov.websocketroomproject.service.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.websocketroomproject.dto.response.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.event.sender.RoomSubscriptionEventSender;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.mapper.ChatUserMapperImpl;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ChatEventMessageServiceImplTest {

    private static final String ROOM_ID = "5";

    private final Faker faker = new Faker();

    @InjectMocks
    ChatEventMessageServiceImpl eventMessageService;

    @Mock
    RoomSubscriptionEventSender roomSubscriptionEventSender;

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

        Mockito.verify(roomSubscriptionEventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptorSet.capture());
        EventMessageDTO<Set<ChatUserDTO>> capturedMsg = eventMessageCaptorSet.getValue();
        Assertions.assertThat(capturedMsg.getEventType()).isEqualTo(EventType.USER_LIST_REFRESH);
        Assertions.assertThat(capturedMsg.getData().getPayload()).contains(chatUserMapper.chatUserToDTO(stubChatUser));
        Mockito.verify(chatUserService, Mockito.times(1)).findChatUsersByRoomId(ROOM_ID);
    }

    @Test
    void shouldCreateEventMessageWithConnectedEventTypeAndCallSender() {
        ChatUser stubChatUser = new ChatUser(faker.name().username(), ROOM_ID, "asdf");

        eventMessageService.sendUserConnectedMessageToAll(stubChatUser);

        Mockito.verify(roomSubscriptionEventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptorChatUser.capture());
        EventMessageDTO<ChatUserDTO> capturedMsg = eventMessageCaptorChatUser.getValue();
        Assertions.assertThat(capturedMsg.getEventType()).isEqualTo(EventType.USER_CONNECT);
        Assertions.assertThat(capturedMsg.getData().getPayload()).isEqualTo(chatUserMapper.chatUserToDTO(stubChatUser));
    }

    @Test
    void shouldCreateEventMessageWithDisconnectedEventTypeAndCallSender() {
        ChatUser stubChatUser = new ChatUser(faker.name().username(), ROOM_ID, "asdf");

        eventMessageService.sendUserDisconnectedMessageToAll(stubChatUser);

        Mockito.verify(roomSubscriptionEventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptorChatUser.capture());
        EventMessageDTO<ChatUserDTO> capturedMsg = eventMessageCaptorChatUser.getValue();
        Assertions.assertThat(capturedMsg.getEventType()).isEqualTo(EventType.USER_DISCONNECT);
        Assertions.assertThat(capturedMsg.getData().getPayload()).isEqualTo(chatUserMapper.chatUserToDTO(stubChatUser));
    }

}