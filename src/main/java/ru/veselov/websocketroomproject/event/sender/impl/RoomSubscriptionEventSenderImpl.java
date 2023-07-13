package ru.veselov.websocketroomproject.event.sender.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.sender.RoomSubscriptionEventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.cache.SubscriptionData;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Set;

/**
 * Send Events to room subscriptions (fluxsinks) stored in cache
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RoomSubscriptionEventSenderImpl implements RoomSubscriptionEventSender {

    private final RoomSubscriptionService roomSubscriptionService;

    @Override
    public void sendEventToRoomSubscriptions(String roomId, EventMessageDTO eventMessageDTO) {
        Set<SubscriptionData> subscriptionsByRoomId = roomSubscriptionService.findSubscriptionsByRoomId(roomId);
        if (!subscriptionsByRoomId.isEmpty()) { //if room is empty we don't need to send anything
            EventType eventType = eventMessageDTO.getEventType();
            ServerSentEvent event = ServerSentEvent.builder()
                    .data(eventMessageDTO.getData())
                    .event(eventType.name())
                    .build();
            subscriptionsByRoomId.forEach(x -> x.getFluxSink().next(event));
            log.info("Message for event [{}] sent to all connected subscriptions of [room #{}]", eventType, roomId);
        }
    }

}
