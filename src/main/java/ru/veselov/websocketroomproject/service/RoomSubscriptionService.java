package ru.veselov.websocketroomproject.service;

import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.cache.SubscriptionData;

import java.util.Optional;
import java.util.Set;

@Service
public interface RoomSubscriptionService {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(SubscriptionData subscriptionData);

    Optional<SubscriptionData> findSubscription(String username, String roomId);

    Set<SubscriptionData> findSubscriptionsByRoomId(String roomId);

}
