package ru.veselov.websocketroomproject.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.service.TagService;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    public static final String URL_PREFIX = "/api/v1/room/tag";

    WebTestClient webTestClient;

    @Mock
    TagService tagService;

    @InjectMocks
    TagController tagController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(tagController).build();
    }

    @Test
    void shouldReturnTags() {
        Mockito.when(tagService.getTags()).thenReturn(Set.of(new Tag("1"), new Tag("2")));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(2);
    }

}
