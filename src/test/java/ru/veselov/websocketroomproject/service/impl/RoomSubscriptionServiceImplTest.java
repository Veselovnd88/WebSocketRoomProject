package ru.veselov.websocketroomproject.service.impl;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


@SpringBootTest
class RoomSubscriptionServiceImplTest {

    private final static String ROOM_ID = "5";

    @Autowired
    RoomSubscriptionService roomSubscriptionService;

    @Test
    @SneakyThrows
    void shouldSaveSubscriptionsToOneRoom() {
        int testNum = 10;
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Field roomSubscriptionMap = roomSubscriptionService.getClass().getDeclaredField("roomSubscriptionsMap");
        roomSubscriptionMap.setAccessible(true);
        Map<String, Set<SubscriptionData>> myMap = (ConcurrentHashMap) roomSubscriptionMap.get(roomSubscriptionService);
        Set<SubscriptionData> subscriptionDataSet = new CopyOnWriteArraySet<>();
        for (int i = 0; i < testNum; i++) {
            subscriptionDataSet.add(
                    new SubscriptionData(TestConstants.TEST_USERNAME + i, ROOM_ID, fluxSink)
            );
        }

        for (SubscriptionData subscriptionData : subscriptionDataSet) {
            roomSubscriptionService.saveSubscription(subscriptionData);
        }

        Assertions.assertThat(myMap).hasSize(1);
        Assertions.assertThat(myMap.get(ROOM_ID)).isNotNull().isInstanceOf(CopyOnWriteArraySet.class);
        Assertions.assertThat(myMap.get(ROOM_ID)).hasSize(testNum).containsAll(subscriptionDataSet);
    }

    @Test
    @SneakyThrows
    void shouldSaveSubscriptionsToDifferentRooms() {
        int testNum = 10;
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Field roomSubscriptionMap = roomSubscriptionService.getClass().getDeclaredField("roomSubscriptionsMap");
        roomSubscriptionMap.setAccessible(true);
        Map<String, Set<SubscriptionData>> myMap = (ConcurrentHashMap) roomSubscriptionMap.get(roomSubscriptionService);
        Set<SubscriptionData> subscriptionDataSet = new CopyOnWriteArraySet<>();
        for (int i = 0; i < testNum; i++) {
            subscriptionDataSet.add(
                    new SubscriptionData(TestConstants.TEST_USERNAME + i, ROOM_ID + i, fluxSink)
            );
        }

        for (SubscriptionData subscriptionData : subscriptionDataSet) {
            roomSubscriptionService.saveSubscription(subscriptionData);
        }

        Assertions.assertThat(myMap).hasSize(testNum);
        Assertions.assertThat(myMap.get(ROOM_ID + 5)).containsAnyElementsOf(subscriptionDataSet).hasSize(1);
    }

    @Test
    @SneakyThrows
    void shouldReplaceSubscription() {
        int testNum = 10;
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Field roomSubscriptionMap = roomSubscriptionService.getClass().getDeclaredField("roomSubscriptionsMap");
        roomSubscriptionMap.setAccessible(true);
        Map<String, Set<SubscriptionData>> myMap = (ConcurrentHashMap) roomSubscriptionMap.get(roomSubscriptionService);
        Set<SubscriptionData> subscriptionDataSet = new CopyOnWriteArraySet<>();
        for (int i = 0; i < testNum; i++) {
            subscriptionDataSet.add(
                    new SubscriptionData(TestConstants.TEST_USERNAME, ROOM_ID, fluxSink)
            );
        }

        for (SubscriptionData subscriptionData : subscriptionDataSet) {
            roomSubscriptionService.saveSubscription(subscriptionData);
        }

        Assertions.assertThat(myMap).hasSize(1);
        Assertions.assertThat(myMap.get(ROOM_ID)).containsAnyElementsOf(subscriptionDataSet).hasSize(1);
    }

    @Test
    void shouldRemoveSubscription() {

    }

}