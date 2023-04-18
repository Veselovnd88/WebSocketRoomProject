package ru.veselov.websocketroomproject.repository;

import com.redis.testcontainers.RedisContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class ChatUserRedisRepositoryTestWithContainers {


    @Container
    private static final RedisContainer REDIS_MASTER_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

    @Container
    private static final RedisContainer REDIS_REPLICA_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:latest")).withExposedPorts(6380);


    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("redis.master.host", REDIS_MASTER_CONTAINER::getHost);
        registry.add("redis.master.port", () -> REDIS_MASTER_CONTAINER.getMappedPort(6379).toString());
        registry.add("redis.replica.host", REDIS_REPLICA_CONTAINER::getHost);
        registry.add("redis.replica.port", () -> REDIS_REPLICA_CONTAINER.getMappedPort(6380).toString());
    }

    @Test
    void givenRedisContainerConfiguredWithDynamicProperties_whenCheckingRunningStatus_thenStatusIsRunning() {
        Assertions.assertThat(REDIS_MASTER_CONTAINER.isRunning()).isTrue();
    }

}