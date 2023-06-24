package ru.veselov.websocketroomproject.event.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.event.RoomSettingsUpdateEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomSettingsUpdateEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(RoomSettingsUpdateEvent roomSettingsUpdateEvent) {
        log.info("Publishing event for changing room settings");
        applicationEventPublisher.publishEvent(roomSettingsUpdateEvent);
    }

}
