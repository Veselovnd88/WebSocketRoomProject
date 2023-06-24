package ru.veselov.websocketroomproject.event;

import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;

public interface RoomSubscriptionEventSender {

    void sendEventToRoomSubscriptions(String roomId, EventMessageDTO eventMessageDTO);

}
