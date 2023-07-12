package ru.veselov.websocketroomproject.event.listener;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;
import ru.veselov.websocketroomproject.event.sender.impl.RoomSubscriptionEventSenderImpl;
import ru.veselov.websocketroomproject.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class RoomDeleteEventListenerTest {

    @Mock
    RoomRepository roomRepository;

    @Mock
    RoomSubscriptionEventSenderImpl roomSubscriptionEventSender;

    @InjectMocks
    RoomDeleteEventListener roomDeleteEventListener;

    @Captor
    ArgumentCaptor<EventMessageDTO<String>> eventMessageCaptor;

    @Test
    void shouldSendEventsAndDeleteRoom() {
        RoomDeleteEvent roomDeleteEvent = new RoomDeleteEvent(TestConstants.ROOM_ID);
        roomDeleteEventListener.onRoomDeleteEvent(roomDeleteEvent);

        Mockito.verify(roomSubscriptionEventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptor.capture());
        Mockito.verify(roomRepository, Mockito.times(1)).deleteById(ArgumentMatchers.any());
        EventMessageDTO<String> captured = eventMessageCaptor.getValue();
        Assertions.assertThat(captured.getEventType()).isEqualTo(EventType.ROOM_DELETE);
    }

}
