package ru.veselov.websocketroomproject.controller;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ServerEventControllerTest {

    private final static String ROOM_ID = "5";

    @Autowired
    WebTestClient webTestClient;

    @Test
    @SneakyThrows
    void shouldReturnSuccessfulCodeAndEventStream() {
        FluxExchangeResult<ServerSentEvent> fluxResult = webTestClient.get().uri("/api/room?roomId=" + ROOM_ID)
                .headers(headers -> headers.setBasicAuth("user1", "secret"))
                //after implementing header with JWT - need to change header
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(ServerSentEvent.class);

        Assertions.assertThat(fluxResult.getResponseBody().blockFirst().event()).isEqualTo("init");
    }

}