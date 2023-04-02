package ru.veselov.websocketroomproject.cache;

import ru.veselov.websocketroomproject.model.SubscriptionData;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionCache {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(SubscriptionData subscriptionData);

    Set<SubscriptionData> findSubscriptionsByRoomId(String roomId);

    Optional<SubscriptionData> findSubscription(String username, String roomId);


}
