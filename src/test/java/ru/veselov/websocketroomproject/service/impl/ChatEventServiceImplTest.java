package ru.veselov.websocketroomproject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.cache.SubscriptionData;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.security.Principal;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class ChatEventServiceImplTest {

    private static final String ROOM_ID = "5";

    @InjectMocks
    private ChatEventServiceImpl chatEventService;

    @Mock
    RoomSubscriptionService subscriptionService;

    @Mock
    Principal principal;

    @BeforeEach
    void init() {
        Mockito.when(principal.getName()).thenReturn(TestConstants.TEST_USERNAME);
    }

    @Test
    void shouldReturnFluxSinkAndSaveSubscription() {
        Flux<ServerSentEvent> eventStream = chatEventService.createEventStream(principal, ROOM_ID);

        StepVerifier.create(eventStream.take(1)).expectNextMatches(event -> event.event().equals("init"))
                .verifyComplete();
        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, Mockito.mock(FluxSink.class));
        Mockito.verify(subscriptionService, Mockito.times(1)).saveSubscription(sub);

    }

    @Test
    void shouldRemoveSubscriptionOnDisposeAndCancel() {
        Flux<ServerSentEvent> eventStream = chatEventService.createEventStream(principal, ROOM_ID);

        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, Mockito.mock(FluxSink.class));
        eventStream.subscribe().dispose();
        Mockito.verify(subscriptionService, Mockito.times(2)).removeSubscription(sub);
    }

}