package ru.veselov.websocketroomproject.cache.impl;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.cache.SubscriptionCache;
import ru.veselov.websocketroomproject.exception.SubscriptionNotFoundException;
import ru.veselov.websocketroomproject.model.SubscriptionData;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@SpringBootTest
@SuppressWarnings({"rawtypes","unchecked"})
class SubscriptionCacheImplTest {

    private final static String ROOM_ID = "5";

    @Autowired
    SubscriptionCache subscriptionCache;

    private Map<String, Set<SubscriptionData>> myMap;

    @BeforeEach
    @SneakyThrows
    void init() {
        Field roomSubscriptionMap = subscriptionCache.getClass().getDeclaredField("roomSubscriptionsMap");
        roomSubscriptionMap.setAccessible(true);
        myMap = (ConcurrentHashMap) roomSubscriptionMap.get(subscriptionCache);
        myMap.clear();
    }

    @Test
    @SneakyThrows
    void shouldSaveSubscriptionsToOneRoom() {
        int roomNum = 1;
        int userNum = 10;
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(userNum, roomNum);

        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }

        Assertions.assertThat(myMap).hasSize(roomNum);
        Assertions.assertThat(myMap.get(String.valueOf(roomNum))).isNotNull().isInstanceOf(CopyOnWriteArraySet.class);
        Assertions.assertThat(myMap.get(String.valueOf(roomNum))).hasSize(userNum).containsAll(subscriptions);
    }

    @Test
    @SneakyThrows
    void shouldSaveSubscriptionsToDifferentRooms() {
        int roomNum = 10;
        int userNum = 1;
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(userNum, roomNum);

        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }

        Assertions.assertThat(myMap).hasSize(roomNum);
        Assertions.assertThat(myMap.get("4")).containsAnyElementsOf(subscriptions).hasSize(1);
    }

    @Test
    @SneakyThrows
    void shouldRemoveSubscriptionAndRemoveRoomWithNoSubscriptions() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        int roomNum = 10;
        int userNum = 5;
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(userNum, roomNum);
        //saving subscriptions
        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }
        //checking if subscriptions were saved correct
        Assertions.assertThat(myMap).hasSize(roomNum);
        Assertions.assertThat(myMap.get("3")).containsAnyElementsOf(subscriptions).hasSize(userNum);
        String roomNumFromDeleteSubs = "5";
        //checking removing one subscription from room
        subscriptionCache.removeSubscription(
                new SubscriptionData("5", roomNumFromDeleteSubs, fluxSink)
        );

        Assertions.assertThat(myMap).hasSize(roomNum);
        Assertions.assertThat(myMap.get(roomNumFromDeleteSubs)).hasSize(userNum - 1);

        //checking removing all subscriptions from room
        for (int i = 1; i < userNum; i++) {
            subscriptionCache.removeSubscription(
                    new SubscriptionData(String.valueOf(i), roomNumFromDeleteSubs, fluxSink)
            );
        }

        Assertions.assertThat(myMap).hasSize(roomNum - 1);
        Assertions.assertThat(myMap.get(ROOM_ID + 0)).isNull();
    }

    @Test
    @SneakyThrows
    void shouldFoundSubscription() {
        int userNum = 5;
        int roomNum = 10;
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(userNum, roomNum);
        //saving subscriptions
        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }

        Optional<SubscriptionData> subscription = subscriptionCache.findSubscription("1", "1");

        Assertions.assertThat(subscription).isPresent();
        Assertions.assertThat(subscription.get().getRoomId()).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void shouldThrowExceptionIfNoRoomFound() {
        int userNum = 5;
        int roomNum = 10;
        Set<SubscriptionData> subscriptions = fillSetWithSubscriptions(userNum, roomNum);
        //saving subscriptions
        for (SubscriptionData subscriptionData : subscriptions) {
            subscriptionCache.saveSubscription(subscriptionData);
        }

        Assertions.assertThatThrownBy(() -> subscriptionCache.findSubscription("5", "50"))
                .isInstanceOf(SubscriptionNotFoundException.class);
    }


    private Set<SubscriptionData> fillSetWithSubscriptions(int userNum, int roomNum) {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Set<SubscriptionData> subscriptionDataSet = new CopyOnWriteArraySet<>();
        for (int i = 1; i < roomNum + 1; i++) {
            for (int j = 1; j < userNum + 1; j++) {
                subscriptionDataSet.add(
                        new SubscriptionData(String.valueOf(j), String.valueOf(i), fluxSink)
                );
            }
        }
        return subscriptionDataSet;
    }

}