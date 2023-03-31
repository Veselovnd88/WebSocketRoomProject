package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;
import ru.veselov.websocketroomproject.service.UserSubscriptionService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("rawtypes")
public class RoomSubscriptionServiceImpl implements RoomSubscriptionService {

    private final Map<String, CopyOnWriteArrayList<SubscriptionData>> roomSubscriptionsMap = new ConcurrentHashMap<>();

    @Override
    public void saveSubscription(SubscriptionData subscriptionData) {
        String username = subscriptionData.getUsername();
        String roomId = subscriptionData.getRoomId();
        //userSubscriptionService.saveSubscription(username, subscriptionData);
        if (!roomSubscriptionsMap.containsKey(roomId)) {
            CopyOnWriteArrayList subscriptionList = new CopyOnWriteArrayList();
            subscriptionList.add(subscriptionData);
            roomSubscriptionsMap.put(roomId,subscriptionList);
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
        return roomSubscriptionsMap.get(roomId);

    }

    @Override
    public SubscriptionData findSubscription(String roomId, String username) {
        return roomSubscriptionsMap.get(roomId)
                .stream()
                .filter(x -> x.getRoomId().equals(roomId) && x.getUsername().equals(username)).findFirst().orElseThrow();
    }

}
