package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.SSEService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SSEServiceImpl implements SSEService {

    private final RoomSubscriptionService roomSubscriptionService;

    @Override
    public Flux<ServerSentEvent> createEventStream(String roomId, String username) {
        return Flux.create(fluxSink -> {
                    log.info("Subscription for user {} of room {} created", username, roomId);
                    fluxSink.onCancel(removeSubscription(username, roomId));
                    fluxSink.onDispose(removeSubscription(username, roomId));
                    fluxSink.next(ServerSentEvent.builder()
                            .event("init")
                            .build());  //send init event to notify successful connection
                    SubscriptionData subscriptionData = new SubscriptionData(username, fluxSink);
                    roomSubscriptionService.saveSubscription(roomId, username, subscriptionData);
                }
        );
    }

    private Disposable removeSubscription(String username, String roomId) {
        return () -> {
            roomSubscriptionService.removeSubscription(roomId, username);
            log.info("Subscription of user {} of room {} removed", username, roomId);
        };
    }
}
