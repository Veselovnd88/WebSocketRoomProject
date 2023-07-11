package ru.veselov.websocketroomproject.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;
import ru.veselov.websocketroomproject.event.sender.RoomSubscriptionEventSender;
import ru.veselov.websocketroomproject.event.task.DeleteRoomTask;
import ru.veselov.websocketroomproject.repository.RoomRepository;

@Component
@Slf4j
public class RoomDeleteEventListener {

    private final RoomSubscriptionEventSender roomSubscriptionEventSender;

    private final RoomRepository roomRepository;

    private final ThreadPoolTaskExecutor timedTaskExecutor;

    public RoomDeleteEventListener(RoomSubscriptionEventSender roomSubscriptionEventSender,
                                   RoomRepository roomRepository,
                                   @Qualifier("timedTaskExecutor") ThreadPoolTaskExecutor timedTaskExecutor) {
        this.roomSubscriptionEventSender = roomSubscriptionEventSender;
        this.roomRepository = roomRepository;
        this.timedTaskExecutor = timedTaskExecutor;
    }

    @EventListener
    public void onRoomDeleteEvent(RoomDeleteEvent roomDeleteEvent) {
        String roomId = roomDeleteEvent.getRoomId();
        roomSubscriptionEventSender.sendEventToRoomSubscriptions(roomId,
                new EventMessageDTO<>(EventType.ROOM_DELETE,
                        "Room will be deleted in 30 seconds"));
        timedTaskExecutor.execute(new DeleteRoomTask(roomId, roomRepository));
    }


}
