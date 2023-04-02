package ru.veselov.websocketroomproject.cache.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.cache.SubscriptionCache;
import ru.veselov.websocketroomproject.exception.SubscriptionNotFoundException;
import ru.veselov.websocketroomproject.model.SubscriptionData;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionCacheImpl implements SubscriptionCache {

    private final Map<String, Set<SubscriptionData>> roomSubscriptionsMap = new ConcurrentHashMap<>();

    @Override
    public void saveSubscription(SubscriptionData subscriptionData) {
        String roomId = subscriptionData.getRoomId();
        if (!roomSubscriptionsMap.containsKey(roomId)) {
            Set<SubscriptionData> subscriptionList = new CopyOnWriteArraySet<>();
            subscriptionList.add(subscriptionData);
            roomSubscriptionsMap.put(roomId, subscriptionList);
        } else {
            roomSubscriptionsMap.get(roomId).add(subscriptionData);
        }
        log.info("New subscription of {} added to room #{}", subscriptionData.getUsername(), roomId);
    }

    @Override
    public void removeSubscription(SubscriptionData subscriptionData) {
        String roomId = subscriptionData.getRoomId();
        boolean removed = roomSubscriptionsMap.get(roomId).remove(subscriptionData);
        if (!removed) {
            return;
        }
        log.info("Subscription of {} removed from room #{}", subscriptionData.getUsername(), roomId);
        if (roomSubscriptionsMap.get(roomId).isEmpty()) {
            roomSubscriptionsMap.remove(roomId);
            log.info("Room #{} removed from subscribe storage", roomId);
        }
    }

    @Override
    public Set<SubscriptionData> findSubscriptionsByRoomId(String roomId) {
        return roomSubscriptionsMap.get(roomId);
    }

    @Override
    public Optional<SubscriptionData> findSubscription(String username, String roomId) {
        Set<SubscriptionData> subscriptions = roomSubscriptionsMap.get(roomId);
        if (subscriptions == null) {
            throw new SubscriptionNotFoundException("No such room for subscriptions");
        }
        return subscriptions.stream()
                .filter(x -> x.getRoomId().equals(roomId) && x.getUsername().equals(username)).findFirst();
    }

}