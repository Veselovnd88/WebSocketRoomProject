package ru.veselov.websocketroomproject.service.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;

import java.util.Set;

@SpringBootTest
@SuppressWarnings({"rawtypes", "unchecked"})
class EventMessageServiceImplTest {

    private static final String ROOM_ID = "5";

    @Autowired
    EventMessageService eventMessageService;

    @Autowired
    ChatUserMapper chatUserMapper;

    private final Faker faker = new Faker();

    @MockBean
    EventSender eventSender;

    @MockBean
    ChatUserService chatUserService;

    @Captor
    ArgumentCaptor<EventMessageDTO<Set<ChatUserDTO>>> eventMessageCaptorSet;

    @Captor
    ArgumentCaptor<EventMessageDTO<ChatUserDTO>> eventMessageCaptorChatUser;

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
        Mockito.when(chatUserService.findChatUsersByRoomId(ROOM_ID)).thenReturn(Set.of(stubChatUser));

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
        Mockito.when(chatUserService.findChatUsersByRoomId(ROOM_ID)).thenReturn(Set.of(stubChatUser));

        eventMessageService.sendUserDisconnectedMessageToAll(stubChatUser);

        Mockito.verify(eventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptorChatUser.capture());
        EventMessageDTO<ChatUserDTO> capturedMsg = eventMessageCaptorChatUser.getValue();
        Assertions.assertThat(capturedMsg.getEventType()).isEqualTo(EventType.DISCONNECTED);
        Assertions.assertThat(capturedMsg.getData()).isEqualTo(chatUserMapper.chatUserToDTO(stubChatUser));
    }

}