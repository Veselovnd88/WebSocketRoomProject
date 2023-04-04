package ru.veselov.websocketroomproject.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

@SpringBootTest
class ChatEventServiceImplTest {

    private static final String ROOM_ID = "5";

    @Autowired
    private ChatEventServiceImpl sseService;

    @MockBean
    RoomSubscriptionService subscriptionService;

    @MockBean
    EventMessageService eventMessageService;

    @Test
    void shouldReturnFluxSinkAndSaveSubscription() {
        Flux<ServerSentEvent> eventStream = sseService.createEventStream(TestConstants.TEST_USERNAME, ROOM_ID);

        StepVerifier.create(eventStream.take(1)).expectNextMatches(event -> event.event().equals("init"))
                .verifyComplete();
        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, Mockito.mock(FluxSink.class));
        Mockito.verify(subscriptionService, Mockito.times(1)).saveSubscription(sub);

    }

    @Test
    void shouldRemoveSubscriptionOnDisposeAndCancel() {
        Flux<ServerSentEvent> eventStream = sseService.createEventStream(TestConstants.TEST_USERNAME, ROOM_ID);

        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, Mockito.mock(FluxSink.class));
        eventStream.subscribe().dispose();
        Mockito.verify(subscriptionService, Mockito.times(2)).removeSubscription(sub);
    }

}