package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.SubscriptionService;

@RestController
@RequestMapping("/api/room")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ServerEventController {

    private final SubscriptionService subscriptionService;

    private final EventMessageService eventMessageService;

    /**
     * Controller handling subscription from client's eventsource,
     * create fluxsink and put it to the storage;
     * Once client connected to the source, we sent him list of users;
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> subscribe(@RequestParam String roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return createFlux(roomId, username);
    }

    private Flux<ServerSentEvent> createFlux(String roomId, String username) {
        return Flux.create(fluxSink -> {
                    log.info("Subscription for user {} of room {} created", username, roomId);
                    fluxSink.onCancel(
                            () -> {
                                subscriptionService.removeSubscription(roomId, username);
                                log.info("Subscription of user {} of room {} removed", username, roomId);
                            }
                    );
                    fluxSink.next(ServerSentEvent.builder()
                            .event("init")
                            .build());  //send init event to notify successful connection
                    SubscriptionData subscriptionData = new SubscriptionData(username, fluxSink, false);
                    subscriptionService.saveSubscription(roomId, username, subscriptionData);
                    eventMessageService.sendUserListToSubscription(roomId, username);
                }
        );
    }

}