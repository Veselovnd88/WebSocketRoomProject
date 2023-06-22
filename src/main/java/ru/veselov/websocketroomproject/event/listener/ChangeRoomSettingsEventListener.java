package ru.veselov.websocketroomproject.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.ChangeRoomSettingsEvent;
import ru.veselov.websocketroomproject.event.EventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.model.Room;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeRoomSettingsEventListener {

    private final EventSender eventSender;

    @EventListener
    public void onChangeRoomSettingsEvent(ChangeRoomSettingsEvent changeRoomSettingsEvent) {
        Room room = changeRoomSettingsEvent.getRoom();
        eventSender.sendEventToRoomSubscriptions(room.getId().toString(),
                new EventMessageDTO<>(EventType.ROOM_SETTING_UPDATE, room));


    }
}
