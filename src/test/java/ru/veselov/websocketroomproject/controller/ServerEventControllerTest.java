package ru.veselov.websocketroomproject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.cache.SubscriptionCache;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@SuppressWarnings("rawtypes")
class ServerEventControllerTest {

    private final static String ROOM_ID = "5";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    SubscriptionCache subscriptionCache;

    @Test
    void shouldReturnSuccessfulCodeAndEventStream() {
        FluxExchangeResult<ServerSentEvent> fluxResult = webTestClient.get().uri("/api/room/event?roomId=" + ROOM_ID)
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(ServerSentEvent.class);

        Flux<ServerSentEvent> responseBody = fluxResult.getResponseBody();
        StepVerifier.create(responseBody)
                .expectNextMatches(x -> x.event().equals("init"))
                .thenCancel().verify();

    }

    @Test
    void shouldThrowExceptionIfNoHeader() {
        webTestClient.get().uri("/api/room/event?roomId=" + ROOM_ID)
                .exchange().expectStatus().is4xxClientError();
    }

}