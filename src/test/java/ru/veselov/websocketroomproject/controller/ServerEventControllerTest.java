package ru.veselov.websocketroomproject.controller;

import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.SubscriptionService;

@WithMockUser(username = "test")
@WebMvcTest(ServerEventController.class)
class ServerEventControllerTest {

    private final static String ROOM_ID = "5";
    private final Faker faker = new Faker();
    @Autowired
    public MockMvc mockMvc;

    @MockBean
    private ChatUserMapper chatUserMapper;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private ChatUserService chatUserService;
    @MockBean
    private EventMessageService eventMessageService;

    @Test
    @SneakyThrows
    void test() {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/room/sse?roomId=" + ROOM_ID))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andReturn();

        Mockito.verify(subscriptionService, Mockito.times(1))
                .saveSubscription(Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(FluxSink.class));

    }

    private ChatUser generateUser() {
        return new ChatUser(faker.name().username(), ROOM_ID, faker.expression("#{letterify '???????'}"));
    }

}