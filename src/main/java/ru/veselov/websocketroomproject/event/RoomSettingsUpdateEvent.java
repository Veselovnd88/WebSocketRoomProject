package ru.veselov.websocketroomproject.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.websocketroomproject.model.Room;

@Data
@AllArgsConstructor
public class RoomSettingsUpdateEvent {

    private Room room;

}
