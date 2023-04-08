package ru.veselov.websocketroomproject.event.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.event.UserDisconnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDisconnectEventHandlerImplTest {

    private static final String ROOM_ID = "5";

    @MockBean
    private EventMessageService eventMessageService;

    @MockBean
    private RoomSubscriptionService roomSubscriptionService;

    @Autowired
    UserDisconnectEventHandler userDisconnectEventHandler;

    @Test
    void shouldCallRoomSubscriptionAndEventMessageServicesAndCompleteStream() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        ChatUser chatUser = new ChatUser(TestConstants.TEST_USERNAME, ROOM_ID, TestConstants.TEST_SESSION_ID);
        Mockito.when(roomSubscriptionService.findSubscription(TestConstants.TEST_USERNAME, ROOM_ID)).thenReturn(
                new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, fluxSink));

        userDisconnectEventHandler.handleDisconnectEvent(chatUser);


        Mockito.verify(eventMessageService, Mockito.times(1)).sendUserDisconnectedMessageToAll(chatUser);
        Mockito.verify(eventMessageService, Mockito.times(1)).sendUserListToAllSubscriptions(ROOM_ID);
        Mockito.verify(fluxSink, Mockito.times(1)).complete();
    }

}