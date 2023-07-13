package ru.veselov.websocketroomproject.event.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.RoomDeleteEvent;

@ExtendWith(MockitoExtension.class)
class RoomDeleteEventPublisherTest {

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    RoomDeleteEventPublisher roomDeleteEventPublisher;

    @Test
    void shouldPublishEvent() {
        RoomDeleteEvent roomDeleteEvent = new RoomDeleteEvent(TestConstants.ROOM_ID);
        roomDeleteEventPublisher.publishEvent(roomDeleteEvent);

        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(roomDeleteEvent);
    }

}