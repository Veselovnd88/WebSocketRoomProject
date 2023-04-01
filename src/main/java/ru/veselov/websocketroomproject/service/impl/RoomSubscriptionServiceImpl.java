package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        return roomSubscriptionsMap.get(roomId)
                .stream()
                .filter(x -> x.getRoomId().equals(roomId) && x.getUsername().equals(username)).findFirst().orElseThrow();
    }

}