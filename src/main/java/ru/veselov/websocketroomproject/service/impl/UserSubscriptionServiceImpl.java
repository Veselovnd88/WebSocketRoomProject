package ru.veselov.websocketroomproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.UserSubscriptionService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final Map<String, CopyOnWriteArrayList<SubscriptionData>> userSubscriptions = new ConcurrentHashMap<>();


    @Override
    public void saveSubscription(String username, SubscriptionData data) {
        if (!userSubscriptions.containsKey(username)) {
            CopyOnWriteArrayList<SubscriptionData> subsList = new CopyOnWriteArrayList<>();
            subsList.add(data);
        } else {
            userSubscriptions.get(username).add(data);
        }
    }

    @Override
    public void removeSubscription(String username) {
        userSubscriptions.remove(username);
    }
}
