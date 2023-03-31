package ru.veselov.websocketroomproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.UserSubscriptionService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final Map<String, SubscriptionData> userSubscriptions = new ConcurrentHashMap<>();


    @Override
    public void saveSubscription(String username, SubscriptionData data) {
        userSubscriptions.put(username, data);
    }

    @Override
    public void removeSubscription(String username) {
        userSubscriptions.remove(username);
    }
}
