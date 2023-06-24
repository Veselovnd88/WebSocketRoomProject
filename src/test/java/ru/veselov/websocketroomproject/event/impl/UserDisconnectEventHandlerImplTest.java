package ru.veselov.websocketroomproject.event.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.cache.SubscriptionData;
import ru.veselov.websocketroomproject.event.handler.impl.UserDisconnectEventHandlerImpl;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatEventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class UserDisconnectEventHandlerImplTest {

    private static final String ROOM_ID = "5";

    @Mock
    private ChatEventMessageService chatEventMessageService;

    @Mock
    private RoomSubscriptionService roomSubscriptionService;

    @InjectMocks
    UserDisconnectEventHandlerImpl userDisconnectEventHandler;

    @Test
    void shouldCompleteSubscriptionAndSendTwoEventMessages() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        ChatUser chatUser = new ChatUser(TestConstants.TEST_USERNAME, ROOM_ID, TestConstants.TEST_SESSION_ID);
        Mockito.when(roomSubscriptionService.findSubscription(TestConstants.TEST_USERNAME, ROOM_ID)).thenReturn(
                Optional.of(new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, fluxSink)
                )
        );

        userDisconnectEventHandler.handleDisconnectEvent(chatUser);

        Mockito.verify(chatEventMessageService, Mockito.times(1)).sendUserDisconnectedMessageToAll(chatUser);
        Mockito.verify(chatEventMessageService, Mockito.times(1)).sendUserListToAllSubscriptions(ROOM_ID);
        Mockito.verify(fluxSink, Mockito.times(1)).complete();
    }

    @Test
    void shouldNotCompleteSubscriptionAndNotSendEventMessages() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        ChatUser chatUser = new ChatUser(TestConstants.TEST_USERNAME, ROOM_ID, TestConstants.TEST_SESSION_ID);
        Mockito.when(roomSubscriptionService.findSubscription(TestConstants.TEST_USERNAME, ROOM_ID)).thenReturn(
                Optional.empty()
        );

        userDisconnectEventHandler.handleDisconnectEvent(chatUser);

        Mockito.verify(chatEventMessageService, Mockito.never()).sendUserDisconnectedMessageToAll(chatUser);
        Mockito.verify(chatEventMessageService, Mockito.never()).sendUserListToAllSubscriptions(ROOM_ID);
        Mockito.verify(fluxSink, Mockito.never()).complete();
    }

}