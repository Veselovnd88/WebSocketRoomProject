package ru.veselov.websocketroomproject.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.cache.SubscriptionData;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;
import ru.veselov.websocketroomproject.event.sender.RoomSubscriptionEventSender;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomDeleteEventListener {

    private final RoomSubscriptionEventSender roomSubscriptionEventSender;

    private final ChatUserService chatUserService;

    private final RoomSubscriptionService roomSubscriptionService;

    @EventListener
    public void onRoomDeleteEvent(RoomDeleteEvent roomDeleteEvent) {
        String roomID = roomDeleteEvent.getRoomEntity().getId().toString();
        roomSubscriptionEventSender.sendEventToRoomSubscriptions(roomID,
                new EventMessageDTO<>(EventType.ROOM_DELETE,
                        "Room will be deleted in 30 seconds"));
        Set<SubscriptionData> subscriptionsByRoomId = roomSubscriptionService.findSubscriptionsByRoomId(roomID);
        subscriptionsByRoomId.forEach(x -> x.getFluxSink().complete());
        Set<ChatUser> chatUsersByRoomId = chatUserService.findChatUsersByRoomId(roomID);
        chatUsersByRoomId.forEach(x -> chatUserService.removeChatUser(x.getSession()));
        //TODO add Scheduled with fixed delay
    }


}
