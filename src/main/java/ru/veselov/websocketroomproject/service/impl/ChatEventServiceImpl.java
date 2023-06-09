package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.cache.SubscriptionData;
import ru.veselov.websocketroomproject.service.ChatEventService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatEventServiceImpl implements ChatEventService {

    private final RoomSubscriptionService roomSubscriptionService;

    @Override
    public Flux<ServerSentEvent> createEventStream(Principal principal, String roomId) {
        return Flux.create(fluxSink -> {
                    String username = principal.getName();
                    log.info("Subscription for [user {} of room {}] created", username, roomId);
                    SubscriptionData subscriptionData = new SubscriptionData(username, roomId, fluxSink);
                    fluxSink.onCancel(removeSubscription(subscriptionData));
                    fluxSink.onDispose(removeSubscription(subscriptionData));
                    fluxSink.next(ServerSentEvent.builder()
                            .event("init")
                            .build());  //send init event to notify successful connection
                    roomSubscriptionService.saveSubscription(subscriptionData);
                }
        );
    }

    private Disposable removeSubscription(SubscriptionData subscriptionData) {
        return () -> roomSubscriptionService.removeSubscription(subscriptionData);
    }
}