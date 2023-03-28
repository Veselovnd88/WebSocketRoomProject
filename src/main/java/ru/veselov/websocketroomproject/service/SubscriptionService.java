package ru.veselov.websocketroomproject.service;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

import java.util.List;

@Service
public interface SubscriptionService {

    void saveSubscription(String roomId, String username, FluxSink<ServerSentEvent> fluxSink);

    void removeSubscription(String roomId, String username);

    List<FluxSink<ServerSentEvent>> findSubscriptionsByRoomId(String roomId);

    FluxSink<ServerSentEvent> findSubscription(String roomId, String username);

}
