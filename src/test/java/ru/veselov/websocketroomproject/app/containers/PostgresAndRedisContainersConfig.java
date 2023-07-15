package ru.veselov.websocketroomproject.app.containers;

import com.redis.testcontainers.RedisContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class PostgresAndRedisContainersConfig extends PostgresContainersConfig{

    @Container
    private static final RedisContainer REDIS_MASTER_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("redis.master.host", REDIS_MASTER_CONTAINER::getHost);
        registry.add("redis.master.port", () -> REDIS_MASTER_CONTAINER.getMappedPort(6379).toString());
    }

}
