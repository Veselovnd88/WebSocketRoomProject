package ru.veselov.websocketroomproject.event.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;
import ru.veselov.websocketroomproject.event.handler.impl.RoomDeleteEventHandlerImpl;
import ru.veselov.websocketroomproject.event.publisher.RoomDeleteEventPublisher;

@ExtendWith(MockitoExtension.class)
class RoomDeleteEventHandlerImplTest {

    @Mock
    RoomDeleteEventPublisher roomDeleteEventPublisher;

    @InjectMocks
    RoomDeleteEventHandlerImpl roomDeleteEventHandler;

    @Captor
    ArgumentCaptor<RoomDeleteEvent> roomDeleteCaptor;

    @Test
    void shouldCreateEventAndPassToPublisher() {
        roomDeleteEventHandler.handleRoomDeleteEvent(TestConstants.ROOM_ID);

        Mockito.verify(roomDeleteEventPublisher, Mockito.times(1)).publishEvent(roomDeleteCaptor.capture());
        RoomDeleteEvent captured = roomDeleteCaptor.getValue();
        Assertions.assertThat(captured.getRoomId()).isEqualTo(TestConstants.ROOM_ID);
    }

}