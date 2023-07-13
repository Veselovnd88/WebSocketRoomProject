package ru.veselov.websocketroomproject.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.dto.response.SseDataDto;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;
import ru.veselov.websocketroomproject.event.sender.RoomSubscriptionEventSender;
import ru.veselov.websocketroomproject.repository.RoomRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomDeleteEventListener {

    private final RoomSubscriptionEventSender roomSubscriptionEventSender;

    private final RoomRepository roomRepository;


    /**
     * Handle roomDeleteEvent, send event to EventSource, after receiving this type of event FrontEnd should
     * close EventSource and WebSocket connection, subscriptions and chatUsers will be deleted accordingly;
     * Then delete room from repository
     */
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRoomDeleteEvent(RoomDeleteEvent roomDeleteEvent) {
        String roomId = roomDeleteEvent.getRoomId();
        log.info("Processing RoomDeleteEvent for [room {}]", roomId);
        roomSubscriptionEventSender.sendEventToRoomSubscriptions(roomId,
                new EventMessageDTO<>(EventType.ROOM_DELETE,
                        new SseDataDto<>("Room deleted", null)));
        roomRepository.deleteById(UUID.fromString(roomId));
        log.info("Room [{}] deleted from repo", roomId);
    }

}
