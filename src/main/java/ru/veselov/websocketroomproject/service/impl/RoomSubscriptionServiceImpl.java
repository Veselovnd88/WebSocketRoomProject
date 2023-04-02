package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.exception.SubscriptionNotFoundException;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomSubscriptionServiceImpl implements RoomSubscriptionService {

    private final Map<String, Set<SubscriptionData>> roomSubscriptionsMap = new ConcurrentHashMap<>();

    @Override
    public void saveSubscription(SubscriptionData subscriptionData) {
        String username = subscriptionData.getUsername();
        String roomId = subscriptionData.getRoomId();
        if (!roomSubscriptionsMap.containsKey(roomId)) {
            Set<SubscriptionData> subscriptionList = new CopyOnWriteArraySet<>();
            subscriptionList.add(subscriptionData);
            roomSubscriptionsMap.put(roomId, subscriptionList);
        } else {
            Set<SubscriptionData> currentSubscriptions = roomSubscriptionsMap.get(roomId);
            checkIfSubscriptionPresent(subscriptionData, currentSubscriptions);
            roomSubscriptionsMap.get(roomId).add(subscriptionData);
        }
        log.info("New subscription of {} added to room #{}", username, roomId);
    }


    @Override
    public void removeSubscription(SubscriptionData subscriptionData) {
        String roomId = subscriptionData.getRoomId();
        roomSubscriptionsMap.get(roomId).remove(subscriptionData);
        log.info("Subscription of {} removed from room #{}", subscriptionData.getUsername(), roomId);
        if (roomSubscriptionsMap.get(roomId).isEmpty()) {
            roomSubscriptionsMap.remove(roomId);
            log.info("Room #{} removed from subscribe storage", roomId);
        }
    }

    @Override
    public List<SubscriptionData> findSubscriptionsByRoomId(String roomId) {
        return roomSubscriptionsMap.get(roomId).stream().toList();
    }

    @Override
    public SubscriptionData findSubscription(String roomId, String username) {
        Set<SubscriptionData> subscriptions = roomSubscriptionsMap.get(roomId);
        if (subscriptions == null) {
            log.warn("No room found with #{}", roomId);
            throw new SubscriptionNotFoundException("No such room number in subscription storage");
        }
        return subscriptions.stream()
                .filter(x -> x.getRoomId().equals(roomId) && x.getUsername().equals(username)).findFirst()
                .orElseThrow(() -> {
                            log.warn("No user's {} subscription found for room", username);
                            return new SubscriptionNotFoundException("No such user found for room");
                        }
                );
    }

    private void checkIfSubscriptionPresent(SubscriptionData subscriptionData, Set<SubscriptionData> subs) {
        if (subs.contains(subscriptionData)) {
            SubscriptionData sub = findSubscription(subscriptionData.getRoomId(), subscriptionData.getUsername());
            FluxSink<ServerSentEvent> fluxSink = sub.getFluxSink();
            fluxSink.complete();
        }
    }

}