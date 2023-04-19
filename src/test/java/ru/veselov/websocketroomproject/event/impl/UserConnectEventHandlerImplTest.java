package ru.veselov.websocketroomproject.event.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.UserConnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.EventMessageService;

@SpringBootTest
class UserConnectEventHandlerImplTest {

    private static final String ROOM_ID = "5";

    @MockBean
    EventMessageService eventMessageService;

    @Autowired
    UserConnectEventHandler userConnectEventHandler;


    @Test
    void shouldSentTwoEventMessages() {
        ChatUser chatUser = new ChatUser(TestConstants.TEST_USERNAME, ROOM_ID, TestConstants.TEST_SESSION_ID);

        userConnectEventHandler.handleConnectEvent(chatUser);

        Mockito.verify(eventMessageService, Mockito.times(1)).sendUserConnectedMessageToAll(chatUser);
        Mockito.verify(eventMessageService, Mockito.times(1)).sendUserListToAllSubscriptions(ROOM_ID);
    }

}