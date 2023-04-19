package ru.veselov.websocketroomproject.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.event.UserConnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.EventMessageService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserConnectEventHandlerImpl implements UserConnectEventHandler {

    private final EventMessageService eventMessageService;

    @Override
    public void handleConnectEvent(ChatUser chatUser) {
        eventMessageService.sendUserListToAllSubscriptions(chatUser.getRoomId());
        eventMessageService.sendUserConnectedMessageToAll(chatUser);
    }

}
