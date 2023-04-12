package ru.veselov.websocketroomproject.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.dto.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventSenderImpl implements EventSender {

    private final RoomSubscriptionService roomSubscriptionService;

    @Override
    public void sendEventToRoomSubscriptions(String roomId, EventMessageDTO eventMessageDTO) {
        Set<SubscriptionData> subscriptionsByRoomId = roomSubscriptionService.findSubscriptionsByRoomId(roomId);
        if (!subscriptionsByRoomId.isEmpty()) { //if room is empty we don't need to send anything
            EventType eventType = eventMessageDTO.getEventType();
            ServerSentEvent event = ServerSentEvent.builder()
                    .data(eventMessageDTO.getMessage())
                    .event(eventType.name())
                    .build();
            subscriptionsByRoomId.forEach(x -> x.getFluxSink().next(event));
            log.info("Message for event {} sent to all connected subscriptions of room #{}", eventType, roomId);
        }
    }

}