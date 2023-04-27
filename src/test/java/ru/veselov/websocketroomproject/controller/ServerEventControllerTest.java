package ru.veselov.websocketroomproject.controller;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
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

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ServerEventControllerTest {

    private final static String ROOM_ID = "5";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    SubscriptionCache subscriptionCache;

    @Test
    void shouldReturnSuccessfulCodeAndEventStream() {
        FluxExchangeResult<ServerSentEvent> fluxResult = webTestClient.get().uri("/api/room?roomId=" + ROOM_ID)
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                //after implementing header with JWT - need to change header
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(ServerSentEvent.class);

        Flux<ServerSentEvent> responseBody = fluxResult.getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext().thenCancel().verify();

        //Assertions.assertThat(fluxResult.getResponseBody().blockFirst().event()).isEqualTo("init");
    }

}