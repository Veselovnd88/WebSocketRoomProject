package ru.veselov.websocketroomproject.event.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.ActiveURLUpdateEvent;

@ExtendWith(MockitoExtension.class)
class ActiveURLUpdateEventPublisherTest {

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    ActiveURLUpdateEventPublisher activeURLUpdateEventPublisher;

    @Test
    void shouldCallEventPublisherToPublishEvent() {
        ActiveURLUpdateEvent activeURLUpdateEvent = new ActiveURLUpdateEvent(TestConstants.ROOM_ID, "https://url.com");
        activeURLUpdateEventPublisher.publishEvent(activeURLUpdateEvent);

        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(activeURLUpdateEvent);
    }

}
