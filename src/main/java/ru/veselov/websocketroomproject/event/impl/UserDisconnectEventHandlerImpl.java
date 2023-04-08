package ru.veselov.websocketroomproject.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.event.UserDisconnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDisconnectEventHandlerImpl implements UserDisconnectEventHandler {

    private final EventMessageService eventMessageService;

    private final RoomSubscriptionService roomSubscriptionService;

    @Override
    public void handleDisconnectEvent(ChatUser chatUser) {
        completeSubscription(chatUser); //complete subscription of removed user
        eventMessageService.sendUserDisconnectedMessageToAll(chatUser);
        eventMessageService.sendUserListToAllSubscriptions(chatUser.getRoomId());
    }

    private void completeSubscription(ChatUser chatUser) {
        SubscriptionData sub = roomSubscriptionService.findSubscription(chatUser.getUsername(), chatUser.getRoomId());
        sub.getFluxSink().complete();
    }
}
