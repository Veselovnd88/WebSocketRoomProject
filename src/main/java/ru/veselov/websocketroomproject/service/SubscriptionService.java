package ru.veselov.websocketroomproject.service;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.model.SubscriptionData;

import java.util.List;

@Service
public interface SubscriptionService {

    void saveSubscription(String roomId, String username, SubscriptionData subscription);

    void removeSubscription(String roomId, String username);

    List<SubscriptionData> findSubscriptionsByRoomId(String roomId);

    SubscriptionData findSubscription(String roomId, String username);

}
