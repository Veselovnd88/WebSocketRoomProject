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
import ru.veselov.websocketroomproject.event.ActiveURLUpdateEvent;
import ru.veselov.websocketroomproject.event.RoomSettingsUpdateEvent;
import ru.veselov.websocketroomproject.event.handler.impl.RoomUpdateHandlerImpl;
import ru.veselov.websocketroomproject.event.publisher.ActiveURLUpdateEventPublisher;
import ru.veselov.websocketroomproject.event.publisher.RoomSettingsUpdateEventPublisher;
import ru.veselov.websocketroomproject.model.Room;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RoomUpdateHandlerImplTest {
    @Mock
    RoomSettingsUpdateEventPublisher roomSettingsUpdateEventPublisher;

    @Mock
    ActiveURLUpdateEventPublisher activeURLUpdateEventPublisher;

    @InjectMocks
    RoomUpdateHandlerImpl roomUpdateHandler;
    @Captor
    ArgumentCaptor<RoomSettingsUpdateEvent> roomSettingsUpdateEventArgumentCaptor;

    @Captor
    ArgumentCaptor<ActiveURLUpdateEvent> activeURLUpdateEventArgumentCaptor;

    @Test
    void shouldCreateRoomSettingsUpdateEventAndCallPublisher() {
        Room room = new Room();
        room.setId(UUID.randomUUID());

        roomUpdateHandler.handleRoomSettingUpdateEvent(room);

        Mockito.verify(roomSettingsUpdateEventPublisher, Mockito.times(1))
                .publishEvent(roomSettingsUpdateEventArgumentCaptor.capture());

        RoomSettingsUpdateEvent captured = roomSettingsUpdateEventArgumentCaptor.getValue();
        Assertions.assertThat(captured.getRoom()).isEqualTo(room);
    }

    @Test
    void shouldCreateActiveURLUpdateEventAndCallPublisher() {
        roomUpdateHandler.handleActiveURLUpdateEvent(TestConstants.ROOM_ID, "https://url.com");

        Mockito.verify(activeURLUpdateEventPublisher, Mockito.times(1))
                .publishEvent(activeURLUpdateEventArgumentCaptor.capture());

        ActiveURLUpdateEvent captured = activeURLUpdateEventArgumentCaptor.getValue();
        Assertions.assertThat(captured.getRoomId()).isEqualTo(TestConstants.ROOM_ID);
        Assertions.assertThat(captured.getUrl()).isEqualTo("https://url.com");
    }

}
