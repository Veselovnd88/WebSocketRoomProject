package ru.veselov.websocketroomproject.event.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.dto.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@SuppressWarnings({"rawtypes", "unchecked"})
class EventSenderImplTest {

    private static final String ROOM_ID = "5";

    private final Faker faker = new Faker();

    @Autowired
    EventSender eventSender;

    @MockBean
    RoomSubscriptionService roomSubscriptionService;

    @Captor
    ArgumentCaptor<ServerSentEvent> sseCaptor;


    @Test
    void shouldSendMessageToAllFluxSinkStreams() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Mockito.when(roomSubscriptionService.findSubscriptionsByRoomId(ROOM_ID)).thenReturn(
                fillSetWithSubscriptions(fluxSink)
        );
        EventMessageDTO eventMessageDTO = new EventMessageDTO(EventType.CONNECTED, "payload");

        eventSender.sendEventToRoomSubscriptions(ROOM_ID, eventMessageDTO);

        Mockito.verify(fluxSink, Mockito.times(10)).next(sseCaptor.capture());
        ServerSentEvent captured = sseCaptor.getValue();
        Assertions.assertThat(captured.event()).isEqualTo(EventType.CONNECTED.name());
        Assertions.assertThat(captured.data()).isEqualTo("payload");
    }

    @Test
    void shouldNotSendMessageIfNoSubscriptions() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Mockito.when(roomSubscriptionService.findSubscriptionsByRoomId(ROOM_ID)).thenReturn(Collections.emptySet());
        EventMessageDTO eventMessageDTO = new EventMessageDTO(EventType.CONNECTED, "payload");

        eventSender.sendEventToRoomSubscriptions(ROOM_ID, eventMessageDTO);

        Mockito.verify(fluxSink, Mockito.never()).next(ArgumentMatchers.any(ServerSentEvent.class));
    }

    private Set<SubscriptionData> fillSetWithSubscriptions(FluxSink fluxSink) {
        return new HashSet<>(
                faker.collection(() -> generateSubscription(fluxSink)).maxLen(10).generate());
    }

    private SubscriptionData generateSubscription(FluxSink fluxSink) {
        return new SubscriptionData(faker.name().username(), ROOM_ID, fluxSink);
    }

}