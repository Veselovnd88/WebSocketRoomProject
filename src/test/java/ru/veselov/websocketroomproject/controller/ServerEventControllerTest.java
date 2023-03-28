package ru.veselov.websocketroomproject.controller;

import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.impl.SubscriptionServiceImpl;

@WithMockUser(username = "test")
@WebMvcTest(ServerEventController.class)
class ServerEventControllerTest {

    private final static String ROOM_ID = "5";

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    private SubscriptionServiceImpl subscriptionService;

    @MockBean
    private EventMessageService eventMessageService;

    @Test
    @SneakyThrows
    void shouldStartAsyncProcessingAndSaveSubscription() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/room/sse?roomId=" + ROOM_ID))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("init")))
                .andReturn();

        Mockito.verify(subscriptionService, Mockito.times(1))
                .saveSubscription(Mockito.anyString(), Mockito.anyString(), Mockito.any(FluxSink.class));

        Mockito.verify(eventMessageService, Mockito.times(1))
                .sendUserListToSubscription(Mockito.anyString(), Mockito.anyString());
    }

}