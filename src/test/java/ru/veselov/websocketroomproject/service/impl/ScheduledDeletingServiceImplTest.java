package ru.veselov.websocketroomproject.service.impl;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.veselov.websocketroomproject.app.containers.PostgresContainersConfig;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.repository.TagRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = {
        "room.delete-empty-room-period-cron= */30 * * * * *",
})
class ScheduledDeletingServiceImplTest extends PostgresContainersConfig {

    public static final String MOVIE = "Movie";

    public static final String CARTOON = "Cartoon";

    public static final String ANIME = "Anime";

    public static final String JAVA = "Java";

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    RoomService roomService;

    @BeforeEach
    void setUp() {
        fillRepoWithRooms();
    }

    @AfterEach
    void clear() {
        roomRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    void shouldDeleteEmptyRoomsWithPeriod() {
        List<RoomEntity> all = roomRepository.findAll();
        Assertions.assertThat(all).hasSize(3);

        Awaitility.await().pollDelay(Duration.ofMillis(5000)).until(() -> true);

        List<Room> afterDelete = roomService.findAll(0, "name", "desc");
        Assertions.assertThat(afterDelete).isEmpty();
    }

    public void fillRepoWithRooms() {
        roomRepository.deleteAll();//clear
        tagRepository.deleteAll();
        tagRepository.save(new TagEntity(MOVIE));
        tagRepository.save(new TagEntity(CARTOON));
        tagRepository.save(new TagEntity(ANIME));
        tagRepository.save(new TagEntity(JAVA));
        Principal principal1 = Mockito.mock(Principal.class);
        Mockito.when(principal1.getName()).thenReturn("xxx");
        Room room1 = Room.builder().playerType(PlayerType.YOUTUBE).name("aaa").isPrivate(false).ownerName("xxx")
                .tags(Set.of(new Tag(MOVIE),
                        new Tag(CARTOON)))
                .build();
        roomService.createRoom(room1, principal1);

        Principal principal2 = Mockito.mock(Principal.class);
        Mockito.when(principal2.getName()).thenReturn("yyy");
        Room room2 = Room.builder().playerType(PlayerType.RUTUBE).name("bbb").isPrivate(false).ownerName("yyy")
                .tags(Set.of(new Tag(MOVIE),
                        new Tag(ANIME)))
                .build();
        roomService.createRoom(room2, principal2);

        Principal principal3 = Mockito.mock(Principal.class);
        Mockito.when(principal3.getName()).thenReturn("zzz");
        Room room3 = Room.builder().playerType(PlayerType.TWITCH).name("ccc").isPrivate(false).ownerName("zzz")
                .tags(Set.of(new Tag(ANIME),
                        new Tag(JAVA)))
                .build();
        roomService.createRoom(room3, principal3);
    }

}