package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.event.SubscriptionData;

public interface UserSubscriptionService {
    void saveSubscription(String username, SubscriptionData data);
    void removeSubscription(String username);

}
