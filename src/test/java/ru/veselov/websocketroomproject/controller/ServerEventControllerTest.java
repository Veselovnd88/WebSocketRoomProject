package ru.veselov.websocketroomproject.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.MockServerConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.impl.SubscriptionServiceImpl;

import java.security.Principal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ServerEventControllerTest {

    private final static String ROOM_ID = "5";
    @Autowired
    WebTestClient webTestClient;

/*
    @Autowired
    public MockMvc mockMvc;
*/
    @Autowired
    ApplicationContext applicationContext;
    @MockBean
    private SubscriptionServiceImpl subscriptionService;


    @Test
    @SneakyThrows
    void shouldStartAsyncProcessingAndSaveSubscription() {
        webTestClient.get().uri("/api/room/sse?roomId=" + ROOM_ID)
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .headers(headers -> headers.setBasicAuth("user1", "secret"))
                .exchange().expectStatus().isOk();



        /*mockMvc.perform(MockMvcRequestBuilders.get("/api/room/sse?roomId=" + ROOM_ID))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("init")))
                .andReturn();
*/
        Mockito.verify(subscriptionService, Mockito.times(1))
                .saveSubscription(Mockito.anyString(), Mockito.anyString(), Mockito.any(SubscriptionData.class));

    }

}