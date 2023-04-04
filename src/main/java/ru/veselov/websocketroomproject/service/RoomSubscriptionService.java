package ru.veselov.websocketroomproject.service;

import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.event.SubscriptionData;

import java.util.Set;

@Service
public interface RoomSubscriptionService {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(SubscriptionData subscriptionData);

    Set<SubscriptionData> findSubscriptionsByRoomId(String roomId);

    SubscriptionData findSubscription(String username, String roomId);

}
