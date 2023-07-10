package ru.veselov.websocketroomproject.event.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;
import ru.veselov.websocketroomproject.event.handler.RoomDeleteEventHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomDeleteEventHandlerImpl implements RoomDeleteEventHandler {
    @Override
    public void handleRoomDeleteEvent(RoomEntity roomEntity) {
        log.info("Handling delete room event");
        RoomDeleteEvent roomDeleteEvent = new RoomDeleteEvent(roomEntity);

    }
}
