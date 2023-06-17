package ru.veselov.websocketroomproject.service.impl;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.cache.SubscriptionCache;
import ru.veselov.websocketroomproject.event.SubscriptionData;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class RoomSubscriptionServiceImplTest {

    private final static String ROOM_ID = "5";

    @InjectMocks
    RoomSubscriptionServiceImpl roomSubscriptionService;

    @Mock
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
    void shouldReturnOptionalSubscription() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        SubscriptionData sub = new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, fluxSink);
        Mockito.when(subscriptionCache.findSubscription(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(sub));

        Optional<SubscriptionData> optional = roomSubscriptionService
                .findSubscription(TestConstants.TEST_USERNAME, ROOM_ID);

        Assertions.assertThat(optional).isPresent();
        Mockito.verify(subscriptionCache, Mockito.times(1)).findSubscription(TestConstants.TEST_USERNAME, ROOM_ID);
    }

    @Test
    void shouldReturnEmptyOptionalIfNoSubscription() {
        Mockito.when(subscriptionCache.findSubscription(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Optional<SubscriptionData> optional = roomSubscriptionService
                .findSubscription(TestConstants.TEST_USERNAME, ROOM_ID);

        Assertions.assertThat(optional).isNotPresent();
        Mockito.verify(subscriptionCache, Mockito.times(1)).findSubscription(TestConstants.TEST_USERNAME, ROOM_ID);
    }

}
