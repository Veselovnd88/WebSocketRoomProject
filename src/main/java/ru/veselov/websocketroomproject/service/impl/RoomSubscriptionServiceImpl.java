package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.cache.SubscriptionCache;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.exception.SubscriptionNotFoundException;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Optional;
import java.util.Set;

/**
 * Service for managing sse subscriptions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomSubscriptionServiceImpl implements RoomSubscriptionService {

    private final SubscriptionCache subscriptionCache;

    @Override
    public void saveSubscription(SubscriptionData subscriptionData) {
        String username = subscriptionData.getUsername();
        String roomId = subscriptionData.getRoomId();
        Optional<SubscriptionData> storedSubscription = subscriptionCache.findSubscription(username, roomId);
        //if flux already exists we should complete it
        storedSubscription.ifPresent(data -> data.getFluxSink().complete());
        subscriptionCache.saveSubscription(subscriptionData);
    }


    @Override
    public void removeSubscription(SubscriptionData subscriptionData) {
        subscriptionCache.removeSubscription(subscriptionData);
    }

    @Override
    public Set<SubscriptionData> findSubscriptionsByRoomId(String roomId) {
        return subscriptionCache.findSubscriptionsByRoomId(roomId);
    }

    @Override
    public SubscriptionData findSubscription(String username, String roomId) {
        Optional<SubscriptionData> subscription = subscriptionCache.findSubscription(username, roomId);
        return subscription.orElseThrow(
                () -> {
                    log.warn("No user's {} subscription found for room", username);
                    return new SubscriptionNotFoundException("No such user found for room");
                }
        );
    }

}