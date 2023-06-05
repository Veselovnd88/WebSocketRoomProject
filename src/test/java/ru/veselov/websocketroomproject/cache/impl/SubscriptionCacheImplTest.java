package ru.veselov.websocketroomproject.cache.impl;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.event.SubscriptionData;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class SubscriptionCacheImplTest {

    SubscriptionCacheImpl subscriptionCache;

    private Map<String, Set<SubscriptionData>> myMap;

    @BeforeEach
    @SneakyThrows
    void init() {
        subscriptionCache = new SubscriptionCacheImpl();
        Field roomSubscriptionMap = subscriptionCache.getClass().getDeclaredField("roomSubscriptionsMap");
        roomSubscriptionMap.setAccessible(true);
        myMap = (ConcurrentHashMap) roomSubscriptionMap.get(subscriptionCache);
        myMap.clear();
    }

    @Test
    @SneakyThrows
    void shouldSaveSubscriptionsToOneRoom() {
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(10, 1);

        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }

        Assertions.assertThat(myMap).hasSize(1);
        Assertions.assertThat(myMap.get("1")).isNotNull().isInstanceOf(CopyOnWriteArraySet.class);
        Assertions.assertThat(myMap.get("1")).hasSize(10).containsAll(subscriptions);
    }

    @Test
    @SneakyThrows
    void shouldSaveSubscriptionsToDifferentRooms() {
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(1, 10);

        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }

        Assertions.assertThat(myMap).hasSize(10);
        Assertions.assertThat(myMap.get("4")).containsAnyElementsOf(subscriptions).hasSize(1);
    }

    @Test
    @SneakyThrows
    void shouldRemoveSubscriptionAndRemoveRoomWithNoSubscriptions() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(5, 10);
        //saving subscriptions
        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }
        //checking if subscriptions were saved correct
        Assertions.assertThat(myMap).hasSize(10);
        Assertions.assertThat(myMap.get("3")).containsAnyElementsOf(subscriptions).hasSize(5);
        //checking removing one subscription from room
        subscriptionCache.removeSubscription(
                new SubscriptionData("user5", "3", fluxSink) // "3" - room number from where we delete user5
        );

        Assertions.assertThat(myMap).hasSize(10);
        Assertions.assertThat(myMap.get("3")).hasSize(4);

        //checking removing all subscriptions from room
        for (int i = 1; i < 5; i++) {
            subscriptionCache.removeSubscription(
                    new SubscriptionData("user" + i, "3", fluxSink)
            );
        }

        Assertions.assertThat(myMap).hasSize(9);
        Assertions.assertThat(myMap.get("3")).isNull();
    }

    @Test
    @SneakyThrows
    void shouldNotThrowNPEIfRoomNotInCacheAndReturn() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(1, 1);

        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }
        //checking removing one subscription from not existed room
        subscriptionCache.removeSubscription(
                new SubscriptionData("user1", "5", fluxSink)
        );

        Assertions.assertThat(myMap).hasSize(1);
        Assertions.assertThat(myMap.get("1")).hasSize(1);
    }

    @Test
    @SneakyThrows
    void shouldFoundSubscription() {
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(5, 10);
        //saving subscriptions
        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }

        Optional<SubscriptionData> subscription = subscriptionCache.findSubscription("user1", "1");

        Assertions.assertThat(subscription).isPresent();
        Assertions.assertThat(subscription.get().getRoomId()).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void shouldReturnEmptyOptional() {
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(5, 10);
        //saving subscriptions
        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }

        Optional<SubscriptionData> optional = subscriptionCache.findSubscription("user5", "100");

        Assertions.assertThat(optional).isEmpty();
    }

    private Set<SubscriptionData> fillSetWithSubscriptions(int userNum, int roomNum) {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Set<SubscriptionData> subscriptionDataSet = new CopyOnWriteArraySet<>();
        for (int i = 1; i < roomNum + 1; i++) {
            for (int j = 1; j < userNum + 1; j++) {
                subscriptionDataSet.add(
                        new SubscriptionData("user" + j, String.valueOf(i), fluxSink)
                );
            }
        }
        return subscriptionDataSet;
    }

}