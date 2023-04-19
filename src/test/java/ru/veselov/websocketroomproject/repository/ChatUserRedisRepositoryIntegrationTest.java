package ru.veselov.websocketroomproject.repository;

import com.redis.testcontainers.RedisContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;

import java.util.Optional;
import java.util.Set;

@SpringBootTest
@Testcontainers
class ChatUserRedisRepositoryIntegrationTest {

    private final static String ROOM_ID = "5";

    @Autowired
    ChatUserRedisRepository chatUserRedisRepository;

    @Container
    private static final RedisContainer REDIS_MASTER_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("redis.master.host", REDIS_MASTER_CONTAINER::getHost);
        registry.add("redis.master.port", () -> REDIS_MASTER_CONTAINER.getMappedPort(6379).toString());
        registry.add("redis.replica.host", REDIS_MASTER_CONTAINER::getHost);
        registry.add("redis.replica.port", () -> REDIS_MASTER_CONTAINER.getMappedPort(6380).toString());
        registry.add("redis.replica.host", REDIS_MASTER_CONTAINER::getHost);
        registry.add("redis.replica.port", () -> REDIS_MASTER_CONTAINER.getMappedPort(6381).toString());
    }

    @Test
    void shouldSaveRetrieveAndDeleteCorrectWithOneRoom() {
        for (int i = 0; i < 10; i++) {
            ChatUserEntity chatUser = new ChatUserEntity();
            chatUser.setRoomId(ROOM_ID);
            chatUser.setUsername(TestConstants.TEST_USERNAME + i);
            chatUser.setSession(TestConstants.TEST_SESSION_ID + i);

            chatUserRedisRepository.save(chatUser);
        }
        Optional<ChatUserEntity> chatUserEntity = chatUserRedisRepository.findById(TestConstants.TEST_SESSION_ID + 0);
        Assertions.assertThat(chatUserEntity).isPresent();
        ChatUserEntity fromRedis = chatUserEntity.get();
        Assertions.assertThat(fromRedis.getSession()).isEqualTo(TestConstants.TEST_SESSION_ID + 0);

        Set<ChatUserEntity> allByRoomId = chatUserRedisRepository.findAllByRoomId(ROOM_ID);
        Assertions.assertThat(allByRoomId).hasSize(10);

        chatUserRedisRepository.deleteById(TestConstants.TEST_SESSION_ID + 0);

        Set<ChatUserEntity> afterDeleting = chatUserRedisRepository.findAllByRoomId(ROOM_ID);
        Assertions.assertThat(afterDeleting).hasSize(9);
    }

    @Test
    void shouldSaveRetrieveAndDeleteCorrectWithDifferentRooms() {
        for (int i = 0; i < 10; i++) {
            ChatUserEntity chatUser = new ChatUserEntity();
            chatUser.setRoomId(ROOM_ID + i);
            chatUser.setUsername(TestConstants.TEST_USERNAME + i);
            chatUser.setSession(TestConstants.TEST_SESSION_ID + i);

            chatUserRedisRepository.save(chatUser);
        }
        Optional<ChatUserEntity> chatUserEntity = chatUserRedisRepository.findById(TestConstants.TEST_SESSION_ID + 0);
        Assertions.assertThat(chatUserEntity).isPresent();
        ChatUserEntity fromRedis = chatUserEntity.get();
        Assertions.assertThat(fromRedis.getSession()).isEqualTo(TestConstants.TEST_SESSION_ID + 0);

        Set<ChatUserEntity> allByRoomId = chatUserRedisRepository.findAllByRoomId("50");
        Assertions.assertThat(allByRoomId).hasSize(1);

        chatUserRedisRepository.deleteById(TestConstants.TEST_SESSION_ID + 0);

        Set<ChatUserEntity> afterDeleting = chatUserRedisRepository.findAllByRoomId("50");
        Assertions.assertThat(afterDeleting).isEmpty();
    }

}