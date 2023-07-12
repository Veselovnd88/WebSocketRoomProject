package ru.veselov.websocketroomproject.event.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;
import ru.veselov.websocketroomproject.event.handler.RoomDeleteEventHandler;
import ru.veselov.websocketroomproject.event.publisher.RoomDeleteEventPublisher;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomDeleteEventHandlerImpl implements RoomDeleteEventHandler {

    private final RoomDeleteEventPublisher roomDeleteEventPublisher;

    @Override
    public void handleRoomDeleteEvent(String roomId) {
        log.info("Handling delete room event");
        RoomDeleteEvent roomDeleteEvent = new RoomDeleteEvent(roomId);
        roomDeleteEventPublisher.publishEvent(roomDeleteEvent);

    }

}
