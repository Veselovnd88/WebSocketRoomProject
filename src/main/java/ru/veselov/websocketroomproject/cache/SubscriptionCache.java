package ru.veselov.websocketroomproject.cache;

import ru.veselov.websocketroomproject.event.SubscriptionData;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionCache {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(SubscriptionData subscriptionData);

    Optional<SubscriptionData> findSubscription(String username, String roomId);

    Set<SubscriptionData> findSubscriptionsByRoomId(String roomId);


}
