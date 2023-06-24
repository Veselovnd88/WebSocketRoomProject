package ru.veselov.websocketroomproject.event.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.veselov.websocketroomproject.event.RoomSettingsUpdateEvent;
import ru.veselov.websocketroomproject.model.Room;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RoomSettingsUpdateEventPublisherTest {

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    RoomSettingsUpdateEventPublisher roomSettingsUpdateEventPublisher;

    @Test
    void shouldCallEventPublisherToPublishEvent() {
        Room room = new Room();
        room.setId(UUID.randomUUID());
        RoomSettingsUpdateEvent roomSettingsUpdateEvent = new RoomSettingsUpdateEvent(room);

        roomSettingsUpdateEventPublisher.publishEvent(roomSettingsUpdateEvent);

        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(roomSettingsUpdateEvent);
    }

}
