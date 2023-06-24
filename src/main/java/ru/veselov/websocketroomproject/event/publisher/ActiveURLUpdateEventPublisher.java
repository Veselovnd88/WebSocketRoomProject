package ru.veselov.websocketroomproject.event.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.event.ActiveURLUpdateEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActiveURLUpdateEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(ActiveURLUpdateEvent activeURLUpdateEvent) {
        log.info("Publishing event for update active URL");
        applicationEventPublisher.publishEvent(activeURLUpdateEvent);
    }
}
