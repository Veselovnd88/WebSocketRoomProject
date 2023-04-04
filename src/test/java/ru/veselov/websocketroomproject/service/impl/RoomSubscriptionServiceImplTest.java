package ru.veselov.websocketroomproject.service.impl;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.cache.SubscriptionCache;
import ru.veselov.websocketroomproject.exception.SubscriptionNotFoundException;
import ru.veselov.websocketroomproject.event.SubscriptionData;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Optional;


@SpringBootTest
@SuppressWarnings({"rawtypes", "unchecked"})
class RoomSubscriptionServiceImplTest {

    private final static String ROOM_ID = "5";

    @Autowired
    RoomSubscriptionService roomSubscriptionService;

    @MockBean
    SubscriptionCache subscriptionCache;

    @Test
    @SneakyThrows
    void shouldSaveSubscriptionsFluxNotComplete() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, fluxSink);
        Optional optional = Optional.empty();
        Mockito.when(subscriptionCache.findSubscription(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(optional);

        roomSubscriptionService.saveSubscription(sub);

        Mockito.verify(subscriptionCache, Mockito.times(1)).saveSubscription(sub);
        Mockito.verify(fluxSink, Mockito.never()).complete();
    }

    @Test
    @SneakyThrows
    void shouldSaveSubscriptionAndCompleteFlux() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, fluxSink);
        Optional optional = Optional.of(sub);
        Mockito.when(subscriptionCache.findSubscription(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(optional);

        roomSubscriptionService.saveSubscription(sub);

        Mockito.verify(subscriptionCache, Mockito.times(1)).saveSubscription(sub);
        Mockito.verify(fluxSink, Mockito.times(1)).complete();
    }

    @Test
    @SneakyThrows
    void shouldRemoveSubscription() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, fluxSink);

        roomSubscriptionService.removeSubscription(sub);

        Mockito.verify(subscriptionCache, Mockito.times(1)).removeSubscription(sub);
    }

    @Test
    @SneakyThrows
    void shouldFoundSubscription() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, fluxSink);
        Optional optional = Optional.of(sub);
        Mockito.when(subscriptionCache.findSubscription(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(optional);

        roomSubscriptionService.findSubscription(TestConstants.TEST_USERNAME, ROOM_ID);

        Mockito.verify(subscriptionCache, Mockito.times(1)).findSubscription(TestConstants.TEST_USERNAME, ROOM_ID);
    }

    @Test
    @SneakyThrows
    void shouldThrowException() {
        Optional optional = Optional.empty();
        Mockito.when(subscriptionCache.findSubscription(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(optional);

        Assertions.assertThatThrownBy(() ->
                        roomSubscriptionService.findSubscription(TestConstants.TEST_USERNAME, ROOM_ID))
                .isInstanceOf(SubscriptionNotFoundException.class);
    }

}