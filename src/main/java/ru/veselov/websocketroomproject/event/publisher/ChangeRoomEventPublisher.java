package ru.veselov.websocketroomproject.event.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.event.ChangeRoomSettingsEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeRoomEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(ChangeRoomSettingsEvent changeRoomSettingsEvent) {
        applicationEventPublisher.publishEvent(changeRoomSettingsEvent);
        log.info("Created event for changing room settings");
    }

}
