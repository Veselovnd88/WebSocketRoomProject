package ru.veselov.websocketroomproject.event.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.event.ActiveURLUpdateEvent;
import ru.veselov.websocketroomproject.event.RoomSettingsUpdateEvent;
import ru.veselov.websocketroomproject.event.handler.RoomUpdateHandler;
import ru.veselov.websocketroomproject.event.publisher.ActiveURLUpdateEventPublisher;
import ru.veselov.websocketroomproject.event.publisher.RoomSettingsUpdateEventPublisher;
import ru.veselov.websocketroomproject.model.Room;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomUpdateHandlerImpl implements RoomUpdateHandler {

    private final RoomSettingsUpdateEventPublisher roomSettingsUpdateEventPublisher;

    private final ActiveURLUpdateEventPublisher activeURLUpdateEventPublisher;

    @Override
    public void handleRoomSettingUpdateEvent(Room room) {
        log.info("Handling room settings update for [room {}], creating Event", room.getId());
        RoomSettingsUpdateEvent roomSettingsUpdateEvent = new RoomSettingsUpdateEvent(room);
        roomSettingsUpdateEventPublisher.publishEvent(roomSettingsUpdateEvent);
    }

    @Override
    public void handleActiveURLUpdateEvent(String roomId, String url) {
        log.info("Handling active URL update for [room {}], creating Event", roomId);
        ActiveURLUpdateEvent activeURLUpdateEvent = new ActiveURLUpdateEvent(roomId, url);
        activeURLUpdateEventPublisher.publishEvent(activeURLUpdateEvent);
    }

}
