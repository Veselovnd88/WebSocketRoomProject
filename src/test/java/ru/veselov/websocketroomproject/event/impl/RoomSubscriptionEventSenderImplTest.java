package ru.veselov.websocketroomproject.event.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.cache.SubscriptionData;
import ru.veselov.websocketroomproject.event.sender.impl.RoomSubscriptionEventSenderImpl;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class RoomSubscriptionEventSenderImplTest {

    private static final String ROOM_ID = "5";

    private final Faker faker = new Faker();

    @Mock
    RoomSubscriptionService roomSubscriptionService;

    @InjectMocks
    RoomSubscriptionEventSenderImpl eventSender;

    @Captor
    ArgumentCaptor<ServerSentEvent> sseCaptor;

    @Test
    void shouldSendMessageToAllFluxSinkStreams() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Mockito.when(roomSubscriptionService.findSubscriptionsByRoomId(ROOM_ID)).thenReturn(
                fillSetWithSubscriptions(fluxSink)
        );
        EventMessageDTO eventMessageDTO = new EventMessageDTO(EventType.USER_CONNECT, "payload");

        eventSender.sendEventToRoomSubscriptions(ROOM_ID, eventMessageDTO);

        Mockito.verify(fluxSink, Mockito.times(10)).next(sseCaptor.capture());
        ServerSentEvent captured = sseCaptor.getValue();
        Assertions.assertThat(captured.event()).isEqualTo(EventType.USER_CONNECT.name());
        Assertions.assertThat(captured.data()).isEqualTo("payload");
    }

    @Test
    void shouldNotSendMessageIfNoSubscriptions() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Mockito.when(roomSubscriptionService.findSubscriptionsByRoomId(ROOM_ID)).thenReturn(Collections.emptySet());
        EventMessageDTO eventMessageDTO = new EventMessageDTO(EventType.USER_CONNECT, "payload");

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
