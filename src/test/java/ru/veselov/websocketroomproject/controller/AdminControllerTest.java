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
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.TagService;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    public static final String URL_PREFIX = "/api/v1/admin";


    WebTestClient webTestClient;

    @Mock
    TagService tagService;

    @Mock
    RoomService roomService;

    @InjectMocks
    AdminController adminController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(adminController).build();
    }

    @Test
    void shouldRemoveTag() {
        String tagToDelete = "Movie";

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/delete").path("/tag").path("/" + tagToDelete).build())
                .exchange().expectStatus().isOk();

        Mockito.verify(tagService, Mockito.times(1)).deleteTag(tagToDelete);
    }

    @Test
    void shouldAddTag() {
        Tag tagToAdd = new Tag("Movie");
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/add").path("/tag")
                        .build())
                .bodyValue(tagToAdd)
                .exchange().expectStatus().isCreated();

        Mockito.verify(tagService, Mockito.times(1)).addTag(tagToAdd.getName());
    }

    @Test
    void shouldDeleteRoom() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/delete").path("/room").path("/" + TestConstants.ROOM_ID)
                        .build())
                .exchange().expectStatus().isNoContent();

        Mockito.verify(roomService, Mockito.times(1)).deleteRoom(TestConstants.ROOM_ID);
    }

}
