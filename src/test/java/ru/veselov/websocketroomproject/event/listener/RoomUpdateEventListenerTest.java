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
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.ActiveURLUpdateEvent;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.event.RoomSettingsUpdateEvent;
import ru.veselov.websocketroomproject.event.sender.impl.RoomSubscriptionEventSenderImpl;
import ru.veselov.websocketroomproject.model.Room;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("rawtypes")
class RoomUpdateEventListenerTest {

    @Mock
    RoomSubscriptionEventSenderImpl roomSubscriptionEventSender;

    @InjectMocks
    RoomUpdateEventListener roomUpdateEventListener;

    @Captor
    ArgumentCaptor<EventMessageDTO> eventMessageCaptor;

    @Test
    void shouldHandleRoomSettingsUpdateEventAndCallEventSender() {
        Room room = new Room();
        room.setId(UUID.randomUUID());
        RoomSettingsUpdateEvent roomSettingsUpdateEvent = new RoomSettingsUpdateEvent(room);

        roomUpdateEventListener.onRoomSettingsUpdateEvent(roomSettingsUpdateEvent);

        Mockito.verify(roomSubscriptionEventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptor.capture());
        EventMessageDTO captured = eventMessageCaptor.getValue();
        Assertions.assertThat(captured.getEventType()).isEqualTo(EventType.ROOM_SETTING_UPDATE);
        Assertions.assertThat(captured.getData()).isEqualTo(room);
    }

    @Test
    void shouldHandeActiveURLUpdateEventAndCallEventSender() {
        ActiveURLUpdateEvent activeURLUpdateEvent = new ActiveURLUpdateEvent(TestConstants.ROOM_ID, "https://url.com");
        roomUpdateEventListener.onUpdateActiveURLEvent(activeURLUpdateEvent);

        Mockito.verify(roomSubscriptionEventSender, Mockito.times(1))
                .sendEventToRoomSubscriptions(ArgumentMatchers.anyString(), eventMessageCaptor.capture());
        EventMessageDTO captured = eventMessageCaptor.getValue();
        Assertions.assertThat(captured.getEventType()).isEqualTo(EventType.ACTIVE_URL_UPDATE);
        Assertions.assertThat(captured.getData()).isEqualTo(new UrlDto("https://url.com"));
    }

}