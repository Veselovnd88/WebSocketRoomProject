package ru.veselov.websocketroomproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.SubscriptionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@SuppressWarnings("rawtypes")
public class SubscriptionServiceImpl implements SubscriptionService {

    private final Map<String, Map<String, SubscriptionData>> roomSubscriptionsMap = new ConcurrentHashMap<>();

    @Override
    public void saveSubscription(String roomId, String username, SubscriptionData subscriptionData) {
        if (roomSubscriptionsMap.containsKey(roomId)) {
            roomSubscriptionsMap.get(roomId).put(username, subscriptionData);
        } else {
            Map<String, SubscriptionData> subscriptions = new ConcurrentHashMap<>();
            subscriptions.put(username, subscriptionData);
            roomSubscriptionsMap.put(roomId, subscriptions);
        }
        log.info("New subscription of {} added to room #{}", username, roomId);
    }

    @Override
    public void removeSubscription(String roomId, String username) {
        Map<String, SubscriptionData> roomSubscriptions = roomSubscriptionsMap.get(roomId);
        if (roomSubscriptions == null) {
            log.info("Subscription already removed");
            return;
        }
        roomSubscriptions.remove(username);
        log.info("Subscription of {} removed from room #{}", username, roomId);
        if (roomSubscriptions.isEmpty()) {
            roomSubscriptionsMap.remove(roomId);
            log.info("Room #{} removed from subscribe storage", roomId);
        }
    }

    @Override
    public List<SubscriptionData> findSubscriptionsByRoomId(String roomId) {
        Map<String, SubscriptionData> stringFluxSinkMap = roomSubscriptionsMap.get(roomId);
        return new ArrayList<>(stringFluxSinkMap.values());
    }

    @Override
    public SubscriptionData findSubscription(String roomId, String username) {
        return roomSubscriptionsMap.get(roomId).get(username);
    }

}
