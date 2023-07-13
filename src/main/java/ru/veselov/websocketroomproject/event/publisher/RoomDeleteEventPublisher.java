package ru.veselov.websocketroomproject.event.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomDeleteEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(RoomDeleteEvent roomDeleteEvent) {
        log.info("Publishing event for deleting [room {}]", roomDeleteEvent.getRoomId());
        applicationEventPublisher.publishEvent(roomDeleteEvent);
    }

}
