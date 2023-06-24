package ru.veselov.websocketroomproject.event.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.event.handler.UserConnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatEventMessageService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserConnectEventHandlerImpl implements UserConnectEventHandler {

    private final ChatEventMessageService chatEventMessageService;

    @Override
    public void handleConnectEvent(ChatUser chatUser) {
        chatEventMessageService.sendUserListToAllSubscriptions(chatUser.getRoomId());
        chatEventMessageService.sendUserConnectedMessageToAll(chatUser);
    }

}
