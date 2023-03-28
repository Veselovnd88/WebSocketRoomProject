package ru.veselov.websocketroomproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.service.SubscriptionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final Map<String, Map<String, FluxSink<ServerSentEvent>>> roomSubscriptionsMap = new ConcurrentHashMap<>();

    @Override
    public void saveSubscription(String roomId, String username, FluxSink<ServerSentEvent> fluxSink) {
        if (roomSubscriptionsMap.containsKey(roomId)) {
            roomSubscriptionsMap.get(roomId).put(username, fluxSink);
        } else {
            Map<String, FluxSink<ServerSentEvent>> userSinks = new ConcurrentHashMap<>();
            userSinks.put(username, fluxSink);
            roomSubscriptionsMap.put(roomId, userSinks);
        }
        log.info("New subscription of {} added to room #{}", username, roomId);
    }

    @Override
    public void removeSubscription(String roomId, String username) {
        roomSubscriptionsMap.get(roomId).remove(username);
        log.info("Subscription of {} removed from room #{}", username, roomId);
    }

    @Override
    public List<FluxSink<ServerSentEvent>> findSubscriptionsByRoomId(String roomId) {
        Map<String, FluxSink<ServerSentEvent>> stringFluxSinkMap = roomSubscriptionsMap.get(roomId);
        return new ArrayList<>(stringFluxSinkMap.values());
    }

    @Override
    public FluxSink<ServerSentEvent> findSubscription(String roomId, String username) {
        return roomSubscriptionsMap.get(roomId).get(username);
    }

}
