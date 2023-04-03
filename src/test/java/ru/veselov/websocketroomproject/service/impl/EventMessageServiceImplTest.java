package ru.veselov.websocketroomproject.service.impl;

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
import ru.veselov.websocketroomproject.controller.EventType;
import ru.veselov.websocketroomproject.dto.EventMessageDTO;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@SuppressWarnings({"rawtypes", "unchecked"})
class EventMessageServiceImplTest {

    private static final String ROOM_ID = "5";

    @Autowired
    EventMessageService eventMessageService;

    private final Faker faker = new Faker();

    @MockBean
    RoomSubscriptionService roomSubscriptionService;

    @MockBean
    ChatUserService chatUserService;

    @Captor
    ArgumentCaptor<EventMessageDTO> eventMsgCaptor;

    @Test
    void shouldSendMessageT() {
        FluxSink fluxSink = Mockito.mock(FluxSink.class);
        Mockito.when(roomSubscriptionService.findSubscriptionsByRoomId(ROOM_ID))
                .thenReturn(fillSetWithSubscriptions(fluxSink));
        Mockito.when(chatUserService.findChatUsersByRoomId(ROOM_ID)).thenReturn(Set.of(
                new ChatUser(faker.name().username(), ROOM_ID, "asdf"))
        );

        eventMessageService.sendUserListToAllSubscriptions(ROOM_ID);

        EventMessageDTO capturedMsg = eventMsgCaptor.capture();
        //Assertions.assertThat(capturedMsg.getEventType()).isEqualTo(EventType.USERS_REFRESHED);
        Mockito.verify(roomSubscriptionService, Mockito.times(1)).findSubscriptionsByRoomId(ROOM_ID);
        Mockito.verify(chatUserService, Mockito.times(1)).findChatUsersByRoomId(ROOM_ID);
        Mockito.verify(fluxSink, Mockito.times(10)).next(ArgumentMatchers.any(ServerSentEvent.class));
    }


    private Set<SubscriptionData> fillSetWithSubscriptions(FluxSink fluxSink) {
        return new HashSet<>(
                faker.collection(() -> generateSubscription(fluxSink)).maxLen(10).generate());
    }

    private SubscriptionData generateSubscription(FluxSink fluxSink) {
        return new SubscriptionData(faker.name().username(), ROOM_ID, fluxSink);
    }

}