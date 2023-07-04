package ru.veselov.websocketroomproject.app;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.app.containers.PostgresContainersConfig;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.repository.TagRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class TagControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/room/tag";

    @Autowired
    TagRepository tagRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void init() {
        tagRepository.deleteAll();
        tagRepository.save(new TagEntity("Movie"));
        tagRepository.save(new TagEntity("Cartoon"));
        tagRepository.save(new TagEntity("Java"));
    }

    @Test
    void shouldReturnTags() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .build())
                .headers(headers -> headers.add(TestConstants.AUTH_HEADER, TestConstants.BEARER_JWT))
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$.[0]").value(Matchers.containsInAnyOrder("Movie","Cartoon","Java"));//FIXME
    }

}
