package ru.veselov.websocketroomproject.cache.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.cache.SubscriptionCache;
import ru.veselov.websocketroomproject.event.SubscriptionData;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Storage of FluxSinks for server events subscriptions of clients for each room
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionCacheImpl implements SubscriptionCache {

    private final Map<String, Set<SubscriptionData>> roomSubscriptionsMap = new ConcurrentHashMap<>();

    @Override
    public void saveSubscription(SubscriptionData subscriptionData) {
        String roomId = subscriptionData.getRoomId();
        roomSubscriptionsMap.putIfAbsent(roomId, new CopyOnWriteArraySet<>());
        Set<SubscriptionData> subscriptionSet = roomSubscriptionsMap.get(roomId);
        subscriptionSet.add(subscriptionData);
        log.info("New subscription of {} added to room #{}", subscriptionData.getUsername(), roomId);
    }

    @Override
    public void removeSubscription(SubscriptionData subscriptionData) {
        String roomId = subscriptionData.getRoomId();
        Set<SubscriptionData> subscriptions = roomSubscriptionsMap.getOrDefault(roomId, Collections.emptySet());
        boolean removed = subscriptions.remove(subscriptionData);
        if (!removed) {
            return; //if subscription already removed don't need to go further
        }
        log.info("Subscription of {} removed from room #{}", subscriptionData.getUsername(), roomId);
        if (roomSubscriptionsMap.get(roomId).isEmpty()) {
            roomSubscriptionsMap.remove(roomId);
            log.info("Room #{} removed from subscribe storage", roomId);
        }
    }

    @Override
    public Optional<SubscriptionData> findSubscription(String username, String roomId) {
        Set<SubscriptionData> subscriptions = roomSubscriptionsMap.get(roomId);
        if (subscriptions == null) {
            return Optional.empty();
        }
        return findSubscriptionByUsernameAndRoomId(subscriptions, username, roomId);
    }

    @Override
    public Set<SubscriptionData> findSubscriptionsByRoomId(String roomId) {
        return roomSubscriptionsMap.getOrDefault(roomId, Collections.emptySet());
    }

    private Optional<SubscriptionData> findSubscriptionByUsernameAndRoomId(
            Set<SubscriptionData> subscriptions, String username, String roomId) {
        return subscriptions.stream()
                .filter(x -> StringUtils.equals(x.getRoomId(), roomId) && StringUtils.equals(x.getUsername(), username))
                .findFirst();
    }

}