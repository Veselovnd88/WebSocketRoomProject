package ru.veselov.websocketroomproject.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.ActiveURLUpdateEvent;
import ru.veselov.websocketroomproject.event.RoomSettingsUpdateEvent;
import ru.veselov.websocketroomproject.event.RoomSubscriptionEventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.model.Room;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomUpdateEventListener {

    private final RoomSubscriptionEventSender roomSubscriptionEventSender;

    @EventListener
    public void onRoomSettingsUpdateEvent(RoomSettingsUpdateEvent roomSettingsUpdateEvent) {
        Room room = roomSettingsUpdateEvent.getRoom();
        log.info("Processing RoomSettingsUpdateEvent for [room {}]", room.getId());
        roomSubscriptionEventSender.sendEventToRoomSubscriptions(room.getId().toString(),
                new EventMessageDTO<>(EventType.ROOM_SETTING_UPDATE, room)
        );
    }

    @EventListener
    public void onUpdateActiveURLEvent(ActiveURLUpdateEvent activeURLUpdateEvent) {
        String roomId = activeURLUpdateEvent.getRoomId();
        UrlDto urlDto = new UrlDto(activeURLUpdateEvent.getUrl());
        log.info("Processing ActiveURLUpdateEvent for [room {}]", roomId);
        roomSubscriptionEventSender.sendEventToRoomSubscriptions(roomId,
                new EventMessageDTO<>(EventType.ACTIVE_URL_UPDATE, urlDto)
        );
    }

}
