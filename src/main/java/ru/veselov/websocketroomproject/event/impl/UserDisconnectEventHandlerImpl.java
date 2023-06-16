package ru.veselov.websocketroomproject.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.event.UserDisconnectEventHandler;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Optional;

/**
 * Handle disconnect event of user, managing completing subscription and notifying users
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserDisconnectEventHandlerImpl implements UserDisconnectEventHandler {

    private final EventMessageService eventMessageService;

    private final RoomSubscriptionService roomSubscriptionService;

    @Override
    public void handleDisconnectEvent(ChatUser chatUser) {
        Optional<SubscriptionData> subOptional = roomSubscriptionService
                .findSubscription(chatUser.getUsername(), chatUser.getRoomId());
        if (subOptional.isPresent()) {
            SubscriptionData sub = subOptional.get();
            completeSubscription(sub); //complete subscription of removed user
            eventMessageService.sendUserDisconnectedMessageToAll(chatUser);
            eventMessageService.sendUserListToAllSubscriptions(chatUser.getRoomId());
        } else {
            log.info("No subscription was stored for [session {}]", chatUser.getSession());
        }

    }

    private void completeSubscription(SubscriptionData sub) {
        sub.getFluxSink().complete();
        log.info("Subscription for [user {}] completed", sub.getUsername());
    }

}

