package ru.veselov.websocketroomproject.event.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.handler.impl.UserConnectEventHandlerImpl;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatEventMessageService;

@ExtendWith(MockitoExtension.class)
class UserConnectEventHandlerImplTest {

    private static final String ROOM_ID = "5";

    @Mock
    ChatEventMessageService chatEventMessageService;

    @InjectMocks
    UserConnectEventHandlerImpl userConnectEventHandler;


    @Test
    void shouldSentTwoEventMessages() {
        ChatUser chatUser = new ChatUser(TestConstants.TEST_USERNAME, ROOM_ID, TestConstants.TEST_SESSION_ID);

        userConnectEventHandler.handleConnectEvent(chatUser);

        Mockito.verify(chatEventMessageService, Mockito.times(1)).sendUserConnectedMessageToAll(chatUser);
        Mockito.verify(chatEventMessageService, Mockito.times(1)).sendUserListToAllSubscriptions(ROOM_ID);
    }

}